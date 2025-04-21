package com.example.gymworkoutapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.data.mappers.isValid
import com.example.gymworkoutapp.data.repository.ExerciseRepository
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.fragments.ExerciseFragment
import com.example.gymworkoutapp.fragments.WorkoutFragment
import com.example.gymworkoutapp.fragments.ProfileFragment
import com.example.gymworkoutapp.fragments.ResultsFragment
import com.example.gymworkoutapp.models.UserData
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var userDataLauncher: ActivityResultLauncher<Intent>
    private var userData: UserData? = null
    private lateinit var userRepository: UserRepository
    private lateinit var exerciseRepository: ExerciseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<ConstraintLayout>(R.id.activity_main_form)
        view.visibility = View.GONE

        userRepository = (application as App).userRepository
        exerciseRepository = (application as App).exerciseRepository

        userDataLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadUserDataAndInitUI()
            }
        }

        loadUserDataAndInitUI()
    }

    private fun loadUserDataAndInitUI() {
        val view = findViewById<ConstraintLayout>(R.id.activity_main_form)

        lifecycleScope.launch {
            userData = userRepository.getUserData()

            if (userData == null || !userData!!.isValid()) {
                userDataLauncher.launch(Intent(this@MainActivity, UserDataActivity::class.java))
                return@launch
            }

            val page = intent.getStringExtra("fragment")
            if (supportFragmentManager.findFragmentById(R.id.flFragment) == null) {
                val startFragment = when (page) {
                    "exercisePage" -> ExerciseFragment(exerciseRepository)
                    "resultsPage" -> ResultsFragment(userRepository)
                    "profilePage" -> ProfileFragment(userRepository)
                    else -> WorkoutFragment(userRepository)
                }
                switchFragment(startFragment)
            }

            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.Exercise -> switchFragment(ExerciseFragment(exerciseRepository))
                    R.id.Results -> switchFragment(ResultsFragment(userRepository))
                    R.id.Profile -> {
                        lifecycleScope.launch {
                            switchFragment(ProfileFragment(userRepository))
                        }
                    }
                    else -> switchFragment(WorkoutFragment(userRepository))
                }
                true
            }

            view.visibility = View.VISIBLE
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }

}