package com.example.gymworkoutapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.data.repository.UserRepository
import com.google.android.material.textfield.TextInputLayout

class AuthFragment(userRepository: UserRepository) : Fragment() {
    private lateinit var signupTitle: TextView
    private lateinit var signupButton: Button
    private lateinit var resetPasswordField: TextInputLayout

    private var mode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mode = arguments?.getString("auth_mode", MODE_LOGIN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signupTitle = view.findViewById(R.id.signup_title)
        signupButton = view.findViewById(R.id.signup_btn)
        resetPasswordField = view.findViewById(R.id.til_reset_password)

//        var text: String
//        var resetPasswordVisibility: Int
//
//        if (mode == MODE_LOGIN) {
//            text = getString(R.string.login)
//            resetPasswordVisibility = View.GONE
//        } else {
//            text = getString(R.string.sign_up)
//            resetPasswordVisibility = View.VISIBLE
//        }
//
//        signupButton.text = text
//        resetPasswordField.visibility = resetPasswordVisibility
    }
//
//    companion object {
//        private const val ARG_MODE = "auth_mode"
//        const val MODE_LOGIN = "login"
//        const val MODE_SIGNUP = "signup"
//
//        fun newInstance(mode: String): AuthFragment {
//            val fragment = AuthFragment((application as App).userRepository)
//            val args = Bundle()
//            args.putString(ARG_MODE, mode)
//            fragment.arguments = args
//            return fragment
//        }
//    }

}