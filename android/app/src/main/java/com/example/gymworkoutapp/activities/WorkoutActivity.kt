package com.example.gymworkoutapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.adapters.WorkoutExerciseAdapter
import com.example.gymworkoutapp.adapters.WorkoutExerciseSetsAdapter.ViewHolder
import com.example.gymworkoutapp.data.mappers.toWorkoutExerciseData
import com.example.gymworkoutapp.data.repository.ExerciseRepository
import com.example.gymworkoutapp.data.repository.WorkoutRepository
import com.example.gymworkoutapp.fragments.ExerciseDialogFragment
import com.example.gymworkoutapp.fragments.RestTimerDialogFragment
import com.example.gymworkoutapp.listeners.OnExerciseSelectedListener
import com.example.gymworkoutapp.models.ExerciseData
import com.example.gymworkoutapp.models.WorkoutData
import com.example.gymworkoutapp.models.WorkoutExerciseData
import com.example.gymworkoutapp.models.WorkoutExerciseSetData
import kotlinx.coroutines.launch
import java.sql.Timestamp

class WorkoutActivity : AppCompatActivity(), OnExerciseSelectedListener {

    private lateinit var workoutRepository: WorkoutRepository
    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var exerciseAdapter: WorkoutExerciseAdapter

    private val started = Timestamp(System.currentTimeMillis())

    private var workout: WorkoutData? = null
    private var exercises = mutableListOf<WorkoutExerciseData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as App
        workoutRepository = app.workoutRepository
        exerciseRepository = app.exerciseRepository
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        lifecycleScope.launch {
            prepareLayout(intent.getIntExtra("workout_id", 0))
        }

        findViewById<Button>(R.id.workout_add_exercise_btn).setOnClickListener {
            val dialog = ExerciseDialogFragment((application as App).exerciseRepository, this)
            dialog.show(supportFragmentManager, "ExerciseDialog")
        }
    }

    private suspend fun prepareLayout(workoutId: Int) {
        workout = workoutRepository.get(workoutId)

        val w = workout

        if (w == null) {
            Toast.makeText(this, "Couldn't load a workout", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            findViewById<TextView>(R.id.workout_name).text = w.name
            exercises = w.exercises
            setExerciseList()
        }
    }

    private fun setExerciseList() {
        if (::exerciseAdapter.isInitialized) {
            exerciseAdapter.notifyItemInserted(exercises.size - 1)
        } else {
            val recycler = findViewById<RecyclerView>(R.id.workout_exercises)
            recycler.layoutManager = LinearLayoutManager(this)
            exerciseAdapter = WorkoutExerciseAdapter(
                exercises,
                this,
                { checkIfWorkoutIsComplete() },
                { showTimer(it) }
            )
            recycler.adapter = exerciseAdapter
        }
    }

    override fun onExerciseSelected(exercise: ExerciseData) {
        lifecycleScope.launch {
            exercise.image = exerciseRepository.getExerciseImage(exercise)
            exercises.add(exercise.toWorkoutExerciseData(exercises.count()))
            setExerciseList()
            supportFragmentManager.findFragmentByTag("ExerciseDialog")?.let {
                (it as DialogFragment).dismiss()
            }
        }
    }

    private fun checkIfWorkoutIsComplete() {
        val allSetsLogged = exercises.all { exercise ->
            exercise.sets.all { it.isLogged }
        }

        findViewById<LinearLayout>(R.id.workout_btn).visibility = if (allSetsLogged) View.VISIBLE else View.GONE
    }

    private fun showTimer(set: WorkoutExerciseSetData) {
        if (set.isLogged) {
            val dialog = RestTimerDialogFragment()
            dialog.show(supportFragmentManager, "ExerciseDialog")
        }
    }

}