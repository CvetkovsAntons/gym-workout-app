package com.example.gymworkoutapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.data.repository.WorkoutRepository
import com.example.gymworkoutapp.models.WorkoutData

class WorkoutConfigActivity : AppCompatActivity() {

    private lateinit var repository: WorkoutRepository

    private var workout: WorkoutData? = null

    private lateinit var workoutNameField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_config)

        workoutNameField = findViewById<EditText>(R.id.workout_config_name)

        workoutNameField.requestFocus()

        findViewById<Button>(R.id.workout_config_btn_cancel).setOnClickListener {
            finish()
        }
    }

}