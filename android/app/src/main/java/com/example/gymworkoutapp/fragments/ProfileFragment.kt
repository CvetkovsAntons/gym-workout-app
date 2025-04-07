package com.example.gymworkoutapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.activities.UserDataActivity
import com.example.gymworkoutapp.data.mappers.isValid
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.models.UserData
import kotlinx.coroutines.launch

class ProfileFragment(
    private var userRepository: UserRepository
) : Fragment() {
//    private lateinit var auth: FirebaseAuth
//    private lateinit var database: FirebaseDatabase

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

        val rootView = view.findViewById<View>(R.id.profile_fragment)
        rootView.visibility = View.GONE

        lifecycleScope.launch {
            val userData = userRepository.getUserData()

            if (userData == null || !userData.isValid()) {
                openProfileEdit()
                return@launch
            }

            setUserData(view, userData)

            rootView.visibility = View.VISIBLE
        }

        view.findViewById<ImageView>(R.id.profile_edit).setOnClickListener {
            openProfileEdit()
        }

//        sign_out_button.setOnClickListener {
//            builder.setTitle("Are You Sure?")
//                .setMessage("Are you sure you want to sign out?")
//                .setCancelable(true)
//                .setPositiveButton("Yes",
//                    DialogInterface.OnClickListener {
//                            dialog, id -> signOutProfile()
//                    })
//                .setNegativeButton("No",
//                    DialogInterface.OnClickListener {
//                            dialog, id -> dialog.cancel()
//                    })
//                .show()
//        }
//
//        delete_account_button.setOnClickListener {
//            builder.setTitle("Are You Sure?")
//                .setMessage("Are you sure you want to delete your profile?")
//                .setCancelable(true)
//                .setPositiveButton("Yes",
//                    DialogInterface.OnClickListener {
//                            dialog, id -> deleteAccount()
//                    })
//                .setNegativeButton("No",
//                    DialogInterface.OnClickListener {
//                            dialog, id -> dialog.cancel()
//                    })
//                .show()
//        }
    }

    private fun setUserData(view: View, userData: UserData) {
        val name = userData.name
        val height = "${userData.height} cm"
        val weight = "${userData.weight} kg"

        view.findViewById<TextView>(R.id.profile_name)?.text = name
        view.findViewById<TextView>(R.id.profile_height)?.text = height
        view.findViewById<TextView>(R.id.profile_weight)?.text = weight
    }

    private fun openProfileEdit() {
        val intent = Intent(activity, UserDataActivity::class.java)
        startActivity(intent)
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