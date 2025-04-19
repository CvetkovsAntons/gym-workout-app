package com.example.gymworkoutapp.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.activities.ExerciseConfigActivity
import com.example.gymworkoutapp.activities.UserDataActivity
import com.example.gymworkoutapp.adapters.ExerciseListAdapter
import com.example.gymworkoutapp.adapters.TextListAdapter
import com.example.gymworkoutapp.data.repository.ExerciseRepository
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.enums.Difficulty
import com.example.gymworkoutapp.enums.Filter
import com.example.gymworkoutapp.models.ExerciseData
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ExerciseFragment(
    private var repository: ExerciseRepository
) : Fragment() {

    private lateinit var buttonFilterYou: MaterialButton
    private lateinit var buttonFilterOthers: MaterialButton
    private lateinit var buttonFilterDownloaded: MaterialButton

    private lateinit var exerciseListAdapter: ExerciseListAdapter

    private var selectedFilters = mutableListOf<Filter>()
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

        buttonFilterYou = view.findViewById(R.id.exercises_btn_created_by_you)
        buttonFilterOthers = view.findViewById(R.id.exercises_btn_created_by_others)
        buttonFilterDownloaded = view.findViewById(R.id.exercises_btn_downloaded)

        setExerciseList()
        setExerciseList()

        buttonFilterYou.setOnClickListener {
            changeFilter(Filter.CREATED_BY_YOU)
        }
        buttonFilterOthers.setOnClickListener {
            changeFilter(Filter.CREATED_BY_OTHERS)
        }
        buttonFilterDownloaded.setOnClickListener {
            changeFilter(Filter.DOWNLOADED)
        }
        view.findViewById<FloatingActionButton>(R.id.exercises_create).setOnClickListener {
            startActivity(Intent(activity, ExerciseConfigActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        prepareFilters()
        setExerciseList()
    }

    private fun setExerciseList() {
        lifecycleScope.launch {
            exercises = repository.getAllExercises()
            Log.d("exercise_id", exercises.toString())
            val recycler = requireView().findViewById<RecyclerView>(R.id.exercises_list)
            recycler.layoutManager = LinearLayoutManager(requireContext())

            if (::exerciseListAdapter.isInitialized) {
                exerciseListAdapter.updateItems(exercises)
            } else {
                exerciseListAdapter = ExerciseListAdapter(exercises)
                recycler.adapter = exerciseListAdapter
            }
        }
    }

    private fun prepareFilters() {
        setFilterColor(Filter.CREATED_BY_YOU)
        setFilterColor(Filter.CREATED_BY_OTHERS)
        setFilterColor(Filter.DOWNLOADED)
    }

    private fun changeFilter(filter: Filter) {
        if (selectedFilters.contains(filter)) {
            selectedFilters.remove(filter)
        } else {
            selectedFilters.add(filter)
        }
        setFilterColor(filter)
        setExerciseList()
    }

    private fun setFilterColor(filter: Filter) {
        val button = when (filter) {
            Filter.CREATED_BY_YOU -> buttonFilterYou
            Filter.CREATED_BY_OTHERS -> buttonFilterOthers
            Filter.DOWNLOADED -> buttonFilterDownloaded
        }

        val selectedColor = ContextCompat.getColor(requireContext(), R.color.primary_darker)
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.primary_lighter)

        button.backgroundTintList = ColorStateList.valueOf(
            if (filter in selectedFilters) selectedColor else unselectedColor
        )
    }

}