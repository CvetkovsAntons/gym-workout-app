package com.example.gymworkoutapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.activities.WorkoutConfigActivity
import com.example.gymworkoutapp.adapters.WorkoutListAdapter
import com.example.gymworkoutapp.data.repository.WorkoutRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class WorkoutFragment(
    private val repository: WorkoutRepository
) : Fragment() {

    private lateinit var adapter: WorkoutListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_workouts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setWorkoutList()

        view.findViewById<FloatingActionButton>(R.id.workouts_create).setOnClickListener {
            startActivity(Intent(activity, WorkoutConfigActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        setWorkoutList()
    }

    private fun setWorkoutList() {
        lifecycleScope.launch {
            val workouts = repository.getAllWorkouts()
            val recycler = requireView().findViewById<RecyclerView>(R.id.workouts_list)
            recycler.layoutManager = LinearLayoutManager(requireContext())

            if (::adapter.isInitialized) {
                adapter.updateItems(workouts)
            } else {
                adapter = WorkoutListAdapter(
                    workouts,
                    requireContext(),
                    lifecycleScope,
                    repository
                )
                recycler.adapter = adapter
            }
        }
    }

}