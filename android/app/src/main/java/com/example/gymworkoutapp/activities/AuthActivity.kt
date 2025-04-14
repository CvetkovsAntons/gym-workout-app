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
import com.example.gymworkoutapp.api.ApiClient
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.managers.TokenManager
import com.example.gymworkoutapp.models.RequestAuth
import com.example.gymworkoutapp.models.ResponseError
import com.example.gymworkoutapp.models.UserData
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository
    private lateinit var tokenManager: TokenManager

    enum class AuthMethod {
        SIGN_IN, LOG_IN
    }

    private var currentAuthMethod: AuthMethod = AuthMethod.SIGN_IN

    override fun onCreate(savedInstanceState: Bundle?) {
        userRepository = (application as App).userRepository
        tokenManager = TokenManager(this)

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
            processErrorResponse(response)
        }
    }

    private suspend fun processLogIn(request: RequestAuth) {
        val response = ApiClient.authService.login(request)

        if (response.isSuccessful) {
            val body = response.body()
            if (body == null || body.accessToken == null || body.refreshToken == null) {
                throw Exception("Invalid authentication response")
            }

            tokenManager.saveTokens(body.accessToken, body.refreshToken)

            setUserData()
            finish()
        } else {
            processErrorResponse(response)
        }
    }

    private suspend fun setUserData() {
        val localUserData = userRepository.getUserData() ?: UserData(
            name = null,
            height = null,
            weight = null,
            dateOfBirth = null
        )

        val token = "Bearer ${tokenManager.getAccessToken()}"
        val onlineUserData = ApiClient.userService.getInfo(token)

        if (onlineUserData.body() == null) {
            ApiClient.userService.putInfo(token, localUserData)
        }

        if (localUserData != null) {

        }
    }

    private fun processErrorResponse(response: Response<out Any>) {
        val errorBody = response.errorBody()?.string()

        val errorMessage = try {
            val gson = Gson()
            val errorResponse = gson.fromJson(errorBody, ResponseError::class.java)
            errorResponse?.error ?: "Unknown error"
        } catch (e: Exception) {
            "Unexpected error"
        }

        Toast.makeText(this@AuthActivity, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
    }

}