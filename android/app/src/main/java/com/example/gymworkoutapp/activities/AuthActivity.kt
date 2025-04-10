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
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.api.ApiClient
import com.example.gymworkoutapp.api.services.AuthRequest
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.logging.Logger

class AuthActivity : AppCompatActivity() {

    enum class AuthMethod {
        SIGN_IN, LOG_IN
    }

    private var currentAuthMethod: AuthMethod = AuthMethod.SIGN_IN

    override fun onCreate(savedInstanceState: Bundle?) {
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
        val request = AuthRequest(
            findViewById<EditText>(R.id.et_signup_email).text.toString().trim(),
            findViewById<EditText>(R.id.et_signup_password).text.toString().trim(),
            findViewById<EditText>(R.id.et_repeat_password).text.toString().trim().takeIf { it.isNotEmpty() }
        )

        lifecycleScope.launch {
            try {
                val response = when (currentAuthMethod) {
                    AuthMethod.SIGN_IN -> ApiClient.authService.register(request)
                    AuthMethod.LOG_IN -> ApiClient.authService.login(request)
                }

                finish()
            } catch (e: Exception) {
                Log.e(e.message, e.message, e)
                Toast.makeText(this@AuthActivity, "Auth failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

}