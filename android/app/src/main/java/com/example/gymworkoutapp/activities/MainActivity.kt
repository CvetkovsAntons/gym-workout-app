package com.example.gymworkoutapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.fragments.ExerciseFragment
import com.example.gymworkoutapp.fragments.WorkoutFragment
import com.example.gymworkoutapp.fragments.ProfileFragment
import com.example.gymworkoutapp.fragments.ResultsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userRepository = (application as App).userRepository

        val page = intent.getStringExtra("fragment")
        if (savedInstanceState == null) {
            val startFragment = when (page) {
                "exercisePage" -> ExerciseFragment(userRepository)
                "resultsPage" -> ResultsFragment(userRepository)
                "profilePage" -> ProfileFragment(userRepository)
                else -> WorkoutFragment(userRepository)
            }
            switchFragment(startFragment)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.Exercise -> switchFragment(ExerciseFragment(userRepository))
                R.id.Results -> switchFragment(ResultsFragment(userRepository))
                R.id.Profile -> switchFragment(ProfileFragment(userRepository))
                else -> switchFragment(WorkoutFragment(userRepository))
            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
    }

}