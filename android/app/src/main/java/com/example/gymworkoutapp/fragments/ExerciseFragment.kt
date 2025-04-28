package com.example.gymworkoutapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.activities.ExerciseConfigActivity
import com.example.gymworkoutapp.adapters.ExerciseListAdapter
import com.example.gymworkoutapp.data.repository.ExerciseRepository
import com.example.gymworkoutapp.listeners.OnExerciseSelectedListener
import com.example.gymworkoutapp.models.ExerciseData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ExerciseFragment(
    private var repository: ExerciseRepository,
    private val isModal: Boolean = false,
    private val exerciseSelectedListener: OnExerciseSelectedListener? = null
) : Fragment() {

    private lateinit var exerciseListAdapter: ExerciseListAdapter

    private var exercises = mutableListOf<ExerciseData>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_exercises, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            setExerciseList()
        }

        val createButton = view.findViewById<FloatingActionButton>(R.id.exercises_create)

        if (isModal) {
            createButton.visibility = View.GONE
            view.findViewById<TextView>(R.id.exercises_title).text = "Choose an exercise"
        } else {
            createButton.setOnClickListener {
                startActivity(Intent(activity, ExerciseConfigActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            setExerciseList()
        }
    }

    private suspend fun setExerciseList() {
        exercises = repository.getAllExercises()

        val recycler = requireView().findViewById<RecyclerView>(R.id.exercises_list)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        if (::exerciseListAdapter.isInitialized) {
            exerciseListAdapter.updateItems(exercises)
        } else {
            exerciseListAdapter = ExerciseListAdapter(
                exercises,
                requireContext(),
                lifecycleScope,
                repository,
                isModal,
                exerciseSelectedListener
            )
            recycler.adapter = exerciseListAdapter
        }
    }

}