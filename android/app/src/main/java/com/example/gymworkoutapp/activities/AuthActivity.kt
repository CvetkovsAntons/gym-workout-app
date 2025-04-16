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
import com.example.gymworkoutapp.models.UserData
import com.example.gymworkoutapp.network.handlers.ApiErrorHandler
import com.example.gymworkoutapp.utils.Helper
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

    enum class AuthMethod {
        SIGN_IN, LOG_IN
    }

    private var currentAuthMethod: AuthMethod = AuthMethod.LOG_IN

    override fun onCreate(savedInstanceState: Bundle?) {
        userRepository = (application as App).userRepository

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        prepareOutput()

        findViewById<TextView>(R.id.auth_method_switch).setOnClickListener {
            switchAuthMethod()
        }

        findViewById<Button>(R.id.auth_button).setOnClickListener {
            auth()
        }
    }

    private fun switchAuthMethod() {
        currentAuthMethod = if (currentAuthMethod == AuthMethod.SIGN_IN) {
            AuthMethod.LOG_IN
        } else {
            AuthMethod.SIGN_IN
        }
        prepareOutput()
    }

    private fun prepareOutput() {
        val title = findViewById<TextView>(R.id.auth_title)
        val button = findViewById<Button>(R.id.auth_button)
        val methodSwitch = findViewById<TextView>(R.id.auth_method_switch)
        val resetPassword = findViewById<TextInputLayout>(R.id.til_signup_password_reset)

        if (currentAuthMethod == AuthMethod.SIGN_IN) {
            title.text = getString(R.string.sign_up)
            button.text = getString(R.string.sign_up)
            methodSwitch.text = "Already have an account? LOG IN"
            resetPassword.visibility = View.VISIBLE
        } else {
            title.text = getString(R.string.log_in)
            button.text = getString(R.string.log_in)
            methodSwitch.text = "Don't have an account yet? SIGN UP"
            resetPassword.visibility = View.GONE
        }
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
            SessionManager.saveTokensFromResponse(response)
            Helper.setMergedData()
            finish()
        } else {
            ApiErrorHandler.showError(this, response)
        }
    }

}