package com.example.gymworkoutapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.adapters.WorkoutExerciseAdapter
import com.example.gymworkoutapp.data.repository.WorkoutRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class WorkoutResultActivity : AppCompatActivity() {

    private lateinit var repository: WorkoutRepository
    private var openedAfterWorkout: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val app = application as App
        repository = app.workoutRepository
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_result)

        openedAfterWorkout = intent.getBooleanExtra("after_workout", false)
        if (openedAfterWorkout) {
            findViewById<LinearLayout>(R.id.workout_result_btn_container).visibility = View.VISIBLE
            findViewById<Button>(R.id.workout_result_continue).setOnClickListener {
                openMainActivity()
            }
        }

        lifecycleScope.launch {
            val workout = repository.getHistoryWorkout(intent.getIntExtra("history_workout_id", 0))
            if (workout == null) {
                Toast.makeText(
                    this@WorkoutResultActivity,
                    "Couldn't load workout result",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            } else {
                findViewById<TextView>(R.id.workout_result_name).text = workout.workout.name
                findViewById<TextView>(R.id.workout_result_status).text = workout.status.name
                findViewById<TextView>(R.id.workout_result_total_volume).text = "${workout.totalVolume} KG"

                val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                findViewById<TextView>(R.id.workout_result_started).text = dateFormatter.format(workout.startedAt)

                if (workout.finishedAt != null) {
                    findViewById<TextView>(R.id.workout_result_finished).text =
                        dateFormatter.format(workout.finishedAt ?: 0)
                }

                val recycler = findViewById<RecyclerView>(R.id.workout_result_recycler)
                recycler.visibility = View.VISIBLE
                recycler.layoutManager = LinearLayoutManager(this@WorkoutResultActivity)
                recycler.adapter = WorkoutExerciseAdapter(workout.exercises, this@WorkoutResultActivity, {}, {}, true)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (openedAfterWorkout) {
                    openMainActivity()
                } else {
                    finish()
                }
            }
        })
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}