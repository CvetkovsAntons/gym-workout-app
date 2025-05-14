package com.example.gymworkoutapp.fragments

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
import com.example.gymworkoutapp.adapters.HistoryWorkoutAdapter
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

class ResultsFragment(private val repository: WorkoutRepository) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val started = repository.countOfStartedWorkouts()
            val finished = repository.countOfFinishedWorkouts()
            view.findViewById<TextView>(R.id.results_started).text = started.toString()
            view.findViewById<TextView>(R.id.results_finished).text = finished.toString()

            val history = repository.getHistory()
            val emptyListText = view.findViewById<TextView>(R.id.results_text)
            val recycler = view.findViewById<RecyclerView>(R.id.results_recycler)

            if (history != null) {
                val context = requireContext()
                emptyListText.visibility = View.GONE
                recycler.visibility = View.VISIBLE
                recycler.layoutManager = LinearLayoutManager(context)
                recycler.adapter = HistoryWorkoutAdapter(history, context)
            } else {
                emptyListText.visibility = View.VISIBLE
                recycler.visibility = View.GONE
            }
        }
    }

}