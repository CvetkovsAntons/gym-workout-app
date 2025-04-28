package com.example.gymworkoutapp.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.adapters.TextListAdapter
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionStep
import com.example.gymworkoutapp.data.mappers.toWorkoutExerciseData
import com.example.gymworkoutapp.data.repository.WorkoutRepository
import com.example.gymworkoutapp.fragments.ExerciseDialogFragment
import com.example.gymworkoutapp.listeners.OnExerciseSelectedListener
import com.example.gymworkoutapp.models.ExerciseData
import com.example.gymworkoutapp.models.WorkoutData
import com.example.gymworkoutapp.models.WorkoutExerciseData
import com.example.gymworkoutapp.utils.toBase64

class WorkoutConfigActivity : AppCompatActivity(), OnExerciseSelectedListener {

    private lateinit var repository: WorkoutRepository

    private var workout: WorkoutData? = null

    private var exerciseAdapter: TextListAdapter<ExerciseExecutionStep>? = null

    private lateinit var workoutNameField: EditText
    private var exercises = mutableListOf<WorkoutExerciseData>()

    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_config)

        workoutNameField = findViewById<EditText>(R.id.workout_config_name)

        workoutNameField.requestFocus()

        findViewById<Button>(R.id.workout_config_btn_cancel).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.workout_config_image_select_btn).setOnClickListener {
            galleryLauncher.launch("image/*")
        }
        findViewById<Button>(R.id.workout_config_exercise_add_btn).setOnClickListener {
            val dialog = ExerciseDialogFragment((application as App).exerciseRepository, this)
            dialog.show(supportFragmentManager, "ExerciseDialog")
        }
    }

    val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent(),
        fun(it: Uri?) {
            val galleryUri = it
            val image = findViewById<ImageView>(R.id.workout_config_image)
            val imageUriTextView = findViewById<TextView>(R.id.workout_config_image_uri)

            try {
                if (galleryUri != null) {
                    image.setImageURI(galleryUri)
                    image.visibility = View.VISIBLE
                    imageUriTextView.text = galleryUri.path
                } else {
                    image.visibility = View.GONE
                    imageUriTextView.text = "Image is not selected..."
                }

                imageUri = galleryUri.toBase64(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    )

    override fun onExerciseSelected(exercise: ExerciseData) {
        exercises.add(exercise.toWorkoutExerciseData(exercises.count()))
//        workoutExerciseAdapter.notifyDataSetChanged()

        supportFragmentManager.findFragmentByTag("ExerciseDialog")?.let {
            (it as DialogFragment).dismiss()
        }
    }

}