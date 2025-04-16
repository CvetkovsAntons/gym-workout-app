package com.example.gymworkoutapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.activities.AuthActivity
import com.example.gymworkoutapp.activities.UserDataActivity
import com.example.gymworkoutapp.adapters.HistoryWeightAdapter
import com.example.gymworkoutapp.auth.SessionManager
import com.example.gymworkoutapp.network.client.ApiClient
import com.example.gymworkoutapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileFragment(private var userRepository: UserRepository) : Fragment() {

    private lateinit var authButton: Button
    private lateinit var deleteAccountButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authButton = view.findViewById<Button>(R.id.profile_auth_button)
        deleteAccountButton = view.findViewById<Button>(R.id.delete_account_button)

        prepareFragment()

        view.findViewById<ImageView>(R.id.profile_edit).setOnClickListener {
            startActivity(Intent(activity, UserDataActivity::class.java))
        }

        authButton.setOnClickListener {
            handleAuthButton()
        }

        deleteAccountButton.setOnClickListener {
            handleDeleteAccountButton()
        }
    }

    override fun onResume() {
        super.onResume()
        prepareFragment()
    }

    private fun handleAuthButton() {
        lifecycleScope.launch {
            if (SessionManager.isAuthenticated()) {
                ApiClient.userService.logout()
                SessionManager.tokenManager().clearTokens()
                prepareFragment()
            } else {
                startActivity(Intent(activity, AuthActivity::class.java))
            }
        }
    }

    private fun handleDeleteAccountButton() {
        lifecycleScope.launch {
            if (SessionManager.isAuthenticated()) {
                ApiClient.userService.delete()
                prepareFragment()
            }
        }
    }

    private fun prepareFragment() {
        lifecycleScope.launch {
            setUserData()
            setHistoryWeight()
            try {
                var authButtonText = getString(R.string.sign_in)
                var deleteAccountButtonVisibility = View.GONE

                if (SessionManager.isAuthenticated()) {
                    authButtonText = getString(R.string.sign_out)
                    deleteAccountButtonVisibility= View.VISIBLE
                }

                authButton.visibility = View.VISIBLE
                authButton.text = authButtonText
                deleteAccountButton.visibility = deleteAccountButtonVisibility
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun setHistoryWeight() {
        val view = requireView()
        val historyWeightRecycler = view.findViewById<RecyclerView>(R.id.history_recycler_view)

        historyWeightRecycler.layoutManager = LinearLayoutManager(requireContext())
        historyWeightRecycler.adapter = HistoryWeightAdapter(userRepository.getWeightHistory())
    }

    private suspend fun setUserData() {
        val view = requireView()
        val userData = userRepository.getUserData()

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
}