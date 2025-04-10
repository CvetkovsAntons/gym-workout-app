package com.example.gymworkoutapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkoutapp.R
import com.google.android.material.textfield.TextInputLayout

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

}