package com.example.gymworkoutapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.auth.SessionManager
import com.example.gymworkoutapp.network.client.ApiClient
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.models.DateOfBirth
import com.example.gymworkoutapp.models.RequestAuth
import com.example.gymworkoutapp.models.ResponseError
import com.example.gymworkoutapp.models.UserData
import com.example.gymworkoutapp.utils.ApiErrorHandler
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

    enum class AuthMethod {
        SIGN_IN, LOG_IN
    }

    private var currentAuthMethod: AuthMethod = AuthMethod.SIGN_IN

    override fun onCreate(savedInstanceState: Bundle?) {
        userRepository = (application as App).userRepository

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val authMethodSwitch = findViewById<TextView>(R.id.auth_method_switch)

        authMethodSwitch.setOnClickListener {
            switchAuthMethod()
        }

        findViewById<Button>(R.id.auth_button).setOnClickListener {
            auth()
        }
    }

    private fun switchAuthMethod() {
        val title = findViewById<TextView>(R.id.auth_title)
        val button = findViewById<Button>(R.id.auth_button)
        val methodSwitch = findViewById<TextView>(R.id.auth_method_switch)
        val resetPassword = findViewById<TextInputLayout>(R.id.til_signup_password_reset)

        var titleText = title.text
        var methodSwitchText = methodSwitch.text
        var resetPasswordVisibility = resetPassword.visibility

        if (currentAuthMethod == AuthMethod.SIGN_IN) {
            titleText = "LOG IN"
            methodSwitchText = "Don't have an account? SIGN IN"
            resetPasswordVisibility = View.GONE
            currentAuthMethod = AuthMethod.LOG_IN
        } else {
            titleText = "SIGN IN"
            methodSwitchText = "Already have an account? LOG IN"
            resetPasswordVisibility = View.VISIBLE
            currentAuthMethod = AuthMethod.SIGN_IN
        }

        title.text = titleText
        button.text = titleText
        methodSwitch.text = methodSwitchText
        resetPassword.visibility = resetPasswordVisibility
    }

    private fun auth() {
        val passwordConfirm = findViewById<EditText>(R.id.et_repeat_password)?.text?.toString()?.trim()
        val request = RequestAuth(
            findViewById<EditText>(R.id.et_signup_email).text.toString().trim(),
            findViewById<EditText>(R.id.et_signup_password).text.toString().trim(),
            if (currentAuthMethod == AuthMethod.SIGN_IN) passwordConfirm else null
        )

        lifecycleScope.launch {
            try {
                when (currentAuthMethod) {
                    AuthMethod.SIGN_IN -> processSignIn(request)
                    AuthMethod.LOG_IN -> processLogIn(request)
                }
            } catch (e: Exception) {
                Toast.makeText(this@AuthActivity, "Unexpected error", Toast.LENGTH_SHORT).show()
                Log.e("Auth", "Exception: ", e)
            }
        }
    }

    private suspend fun processSignIn(request: RequestAuth) {
        val response = ApiClient.authService.register(request)

        if (response.isSuccessful) {
            processLogIn(request)
        } else {
            ApiErrorHandler.showError(this, response)
        }
    }

    private suspend fun processLogIn(request: RequestAuth) {
        val response = ApiClient.authService.login(request)

        if (response.isSuccessful) {
            val body = response.body()
            if (body == null || body.accessToken == null || body.refreshToken == null) {
                throw Exception("Invalid authentication response")
            }

            SessionManager.tokenManager().saveTokens(body.accessToken, body.refreshToken)

            setUserData()
            finish()
        } else {
            ApiErrorHandler.showError(this, response)
        }
    }

    private suspend fun setUserData() {
        var local = userRepository.getUserData()
        val localExists = local != null
        if (!localExists) {
            local = UserData(name = null, height = null, weight = null, dateOfBirth = null)
        }

        val online = ApiClient.userService.getInfo().body()

        val localDob = local?.dateOfBirth
        val onlineDob = online?.dateOfBirth

        var dateOfBirth = DateOfBirth(
            day = mergeUserDataField(localDob?.day, onlineDob?.day).toString().toIntOrNull(),
            month = mergeUserDataField(localDob?.month, onlineDob?.month).toString().toIntOrNull(),
            year = mergeUserDataField(localDob?.year, onlineDob?.year).toString().toIntOrNull()
        )

        val mergedUserData = UserData(
            name = mergeUserDataField(local?.name, online?.name)?.toString(),
            height = mergeUserDataField(local?.height, online?.height).toString().toFloatOrNull(),
            weight = mergeUserDataField(local?.weight, online?.weight).toString().toFloatOrNull(),
            dateOfBirth = dateOfBirth
        )

        if (localExists) {
            userRepository.updateUserData(mergedUserData)
        } else {
            userRepository.insertUserData(mergedUserData)
        }

        ApiClient.userService.putInfo(mergedUserData)
    }

    private fun mergeUserDataField(local: Any?, online: Any ?): Any? {
        return if (local == null && online != null) online else local
    }

}