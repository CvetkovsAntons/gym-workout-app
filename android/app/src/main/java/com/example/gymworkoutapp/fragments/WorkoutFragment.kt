package com.example.gymworkoutapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.activities.WorkoutConfigActivity
import com.example.gymworkoutapp.data.repository.WorkoutRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WorkoutFragment(repository: WorkoutRepository) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<FloatingActionButton>(R.id.workouts_create).setOnClickListener {
            startActivity(Intent(activity, WorkoutConfigActivity::class.java))
        }
    }

}