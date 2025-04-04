package com.example.gymworkoutapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.fragments.ExploreFragment
import com.example.gymworkoutapp.fragments.HomeFragment
import com.example.gymworkoutapp.fragments.ProfileFragment
import com.example.gymworkoutapp.fragments.ResultsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val page = intent.getStringExtra("fragment")
        if (savedInstanceState == null) {
            val startFragment = when (page) {
                "explorePage" -> ExploreFragment()
                "resultsPage" -> ResultsFragment()
                "profilePage" -> ProfileFragment()
                else -> HomeFragment()
            }
            switchFragment(startFragment)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.Explore -> switchFragment(ExploreFragment())
                R.id.Results -> switchFragment(ResultsFragment())
                R.id.Profile -> switchFragment(ProfileFragment())
                else -> switchFragment(HomeFragment())
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