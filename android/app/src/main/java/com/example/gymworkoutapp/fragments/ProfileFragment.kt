package com.example.gymworkoutapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.activities.AuthActivity
import com.example.gymworkoutapp.activities.UserDataActivity
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.models.UserData
import kotlinx.coroutines.launch
import kotlin.math.log

class ProfileFragment(
    private var userRepository: UserRepository,
    private val userData: UserData?
) : Fragment() {

    private lateinit var builder : AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var loginButton = view.findViewById<Button>(R.id.log_in_button)

        setUserData(view, userData)

        loginButton.visibility = View.VISIBLE

        view.findViewById<ImageView>(R.id.profile_edit).setOnClickListener {
            startActivity(Intent(activity, UserDataActivity::class.java))
        }

        loginButton.setOnClickListener {
            startActivity(Intent(activity, AuthActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val userData = userRepository.getUserData()
            if (userData != null) {
                setUserData(requireView(), userData)
            }
        }
    }

    private fun setUserData(view: View, userData: UserData?) {
        var name = "-"
        var height = "-"
        var weight = "-"
        var dob = "-"

        if (userData != null) {
            if (userData.name != null) {
                name = userData.name
            }
            if (userData.height != null) {
                height = "${userData.height} cm"
            }
            if (userData.weight != null) {
                weight = "${userData.weight} kg"
            }
            if (userData.dateOfBirth != null) {
                val day = userData.dateOfBirth.day
                val month = userData.dateOfBirth.month
                val year = userData.dateOfBirth.year
                dob = "${day}/${month}/${year}"
            }
        }

        view.findViewById<TextView>(R.id.profile_name)?.text = name
        view.findViewById<TextView>(R.id.profile_height)?.text = height
        view.findViewById<TextView>(R.id.profile_weight)?.text = weight
        view.findViewById<TextView>(R.id.profile_dob)?.text = dob
    }

    private fun login() {

    }

//    private fun signOutProfile() {
//        auth.signOut()
//        val intent = Intent(requireActivity(), SignUpActivity::class.java)
//        startActivity(intent)
//        requireActivity().finish()
//    }
//
//    private fun deleteAccount() {
//        val intent = Intent(requireActivity(), SignUpActivity::class.java)
//        intent.putExtra("delete", "delete")
//        intent.putExtra("user", auth.currentUser!!.uid)
//        startActivity(intent)
//        requireActivity().finish()
//    }
}