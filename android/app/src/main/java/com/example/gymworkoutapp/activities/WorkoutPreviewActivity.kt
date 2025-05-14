package com.example.gymworkoutapp.activities

import android.content.Intent
import android.os.Bundle
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

open class WorkoutPreviewActivity : AppCompatActivity(), OnExerciseSelectedListener {

    protected lateinit var workoutRepository: WorkoutRepository
    protected lateinit var exerciseRepository: ExerciseRepository
    protected lateinit var exerciseAdapter: WorkoutExerciseAdapter

    protected var workout: WorkoutData? = null
    protected open var isViewMode = true

    protected lateinit var buttonContainer: LinearLayout
    protected lateinit var addExerciseButton: Button
    protected lateinit var startFinishButton: Button

    final override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as App
        workoutRepository = app.workoutRepository
        exerciseRepository = app.exerciseRepository
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        buttonContainer = findViewById(R.id.workout_btn)
        addExerciseButton = findViewById(R.id.workout_add_exercise_btn)
        startFinishButton = findViewById(R.id.workout_start_finish_btn)

        lifecycleScope.launch {
            prepareLayout()
        }
    }

    private fun prepareButtons() {
        if (isViewMode) {
            addExerciseButton.visibility = View.GONE
            startFinishButton.text = "START"

            val w = workout
            if (w != null) {
                startFinishButton.setOnClickListener {
                    val intent = Intent(this, WorkoutActivity::class.java)
                    intent.putExtra("workout_id", w.id)
                    startActivity(intent)
                }

                buttonContainer.visibility = View.VISIBLE
            }
        } else {
            addExerciseButton.setOnClickListener {
                val dialog = ExerciseDialogFragment((application as App).exerciseRepository, this)
                dialog.show(supportFragmentManager, "ExerciseDialog")
            }

            checkIfWorkoutIsCompleted()
        }
    }

    private suspend fun prepareLayout() {
        prepareWorkout()

        val w = workout

        if (w == null) {
            Toast.makeText(this, "Couldn't load a workout", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            findViewById<TextView>(R.id.workout_name).text = w.name
            prepareButtons()
            setExerciseList()
        }
    }

    protected open suspend fun prepareWorkout() {
        workout = workoutRepository.get(intent.getIntExtra("workout_id", 0))
    }

    override fun onExerciseSelected(exercise: ExerciseData) {
        lifecycleScope.launch {
            exercise.image = exerciseRepository.getExerciseImage(exercise)
            workout?.exercises?.add(exercise.toWorkoutExerciseData(workout?.exercises?.count() ?: 0))
            setExerciseList()
            supportFragmentManager.findFragmentByTag("ExerciseDialog")?.let {
                (it as DialogFragment).dismiss()
            }
        }
    }

    protected fun setExerciseList() {
        if (::exerciseAdapter.isInitialized) {
            exerciseAdapter.notifyItemInserted((workout?.exercises?.size ?: 0) - 1)
        } else {
            val recycler = findViewById<RecyclerView>(R.id.workout_exercises)
            recycler.layoutManager = LinearLayoutManager(this)
            exerciseAdapter = WorkoutExerciseAdapter(
                workout?.exercises ?: mutableListOf<WorkoutExerciseData>(),
                this,
                { checkIfWorkoutIsCompleted() },
                { if (it.isLogged) showTimer(it) },
                isViewMode
            )
            recycler.adapter = exerciseAdapter
        }
    }

    protected fun allSetsLogged(): Boolean {
        return workout?.exercises?.all { exercise ->
            exercise.sets.all { it.isLogged }
        } == true
    }

    private fun checkIfWorkoutIsCompleted() {
        if (allSetsLogged()) {
            buttonContainer.visibility = View.VISIBLE
            startFinishButton.setOnClickListener {
               finishWorkout()
            }
        } else {
            buttonContainer.visibility = View.GONE
        }
    }

    protected open fun finishWorkout() {}

    protected open fun showTimer(set: WorkoutExerciseSetData) {
        if (allSetsLogged()) {
            return
        }

        val dialog = RestTimerDialogFragment()
        dialog.show(supportFragmentManager, "ExerciseDialog")
    }

}