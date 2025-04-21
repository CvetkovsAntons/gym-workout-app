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
import com.example.gymworkoutapp.data.sync.UserDataSyncManager
import com.example.gymworkoutapp.enums.AuthMethod
import com.example.gymworkoutapp.models.RequestAuth
import com.example.gymworkoutapp.network.handlers.ApiErrorHandler
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

    private var currentAuthMethod: AuthMethod = AuthMethod.LOG_IN

    override fun onCreate(savedInstanceState: Bundle?) {
        userRepository = (application as App).userRepository

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        prepareLayout()

        findViewById<TextView>(R.id.auth_method_switch).setOnClickListener {
            currentAuthMethod = if (currentAuthMethod == AuthMethod.SIGN_IN) {
                AuthMethod.LOG_IN
            } else {
                AuthMethod.SIGN_IN
            }
            prepareLayout()
        }

        findViewById<Button>(R.id.auth_button).setOnClickListener {
            auth()
        }
    }

    private fun prepareLayout() {
        val titleView = findViewById<TextView>(R.id.auth_title)
        val authButton = findViewById<Button>(R.id.auth_button)
        val switchText = findViewById<TextView>(R.id.auth_method_switch)
        val passwordResetInput = findViewById<TextInputLayout>(R.id.til_signup_password_reset)

        when (currentAuthMethod) {
            AuthMethod.SIGN_IN -> {
                titleView.text = getString(R.string.sign_up)
                authButton.text = getString(R.string.sign_up)
                switchText.text = getString(R.string.auth_switch_signup)
                passwordResetInput.visibility = View.VISIBLE
            }
            AuthMethod.LOG_IN -> {
                titleView.text = getString(R.string.log_in)
                authButton.text = getString(R.string.log_in)
                switchText.text = getString(R.string.auth_switch_login)
                passwordResetInput.visibility = View.GONE
            }
        }
    }

    private fun auth() {
        val passwordConfirm = getRequestParam(R.id.et_repeat_password)
        val request = RequestAuth(
            getRequestParam(R.id.et_signup_email),
            getRequestParam(R.id.et_signup_password),
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

    private fun getRequestParam(fieldId: Int): String {
        return findViewById<EditText>(fieldId).text.toString().trim()
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
            SessionManager.saveTokensFromResponse(response)
            UserDataSyncManager(userRepository, ApiClient.userService).merge()
            finish()
        } else {
            ApiErrorHandler.showError(this, response)
        }
    }

}