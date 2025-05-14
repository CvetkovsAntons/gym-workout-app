package com.example.gymworkoutapp.activities

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.gymworkoutapp.listeners.OnExerciseSelectedListener
import com.example.gymworkoutapp.models.ExerciseData
import com.example.gymworkoutapp.models.WorkoutData
import com.example.gymworkoutapp.models.WorkoutExerciseData
import com.example.gymworkoutapp.utils.base64ToBitmap
import com.example.gymworkoutapp.utils.toBase64
import kotlinx.coroutines.launch

class WorkoutConfigActivity : AppCompatActivity(), OnExerciseSelectedListener {

    private lateinit var workoutRepository: WorkoutRepository
    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var exerciseAdapter: WorkoutExerciseAdapter
    private lateinit var workoutNameField: EditText

    private var workout: WorkoutData? = null
    private var exercises = mutableListOf<WorkoutExerciseData>()
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        workoutRepository = (application as App).workoutRepository
        exerciseRepository = (application as App).exerciseRepository

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_config)

        workout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("workout", WorkoutData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("workout")
        }

        workoutNameField = findViewById<EditText>(R.id.workout_config_name)
        workoutNameField.requestFocus()

        lifecycleScope.launch {
            workout?.let {
                workoutNameField.setText(it.name)
                findViewById<EditText>(R.id.workout_config_description).setText(it.description)

                imageUri = workoutRepository.getWorkoutImage(it)
                val image = imageUri?.base64ToBitmap()
                val imageView = findViewById<ImageView>(R.id.workout_config_image)
                val imageUriTextView = findViewById<TextView>(R.id.workout_config_image_uri)

                if (image != null) {
                    try {
                        imageView.setImageBitmap(image)
                        imageView.visibility = View.VISIBLE
                        imageUriTextView.text = "Loaded from saved image"
                    } catch (e: Exception) {
                        e.printStackTrace()
                        imageView.visibility = View.GONE
                        imageUriTextView.text = "Failed to load image"
                    }
                } else {
                    imageView.visibility = View.GONE
                    imageUriTextView.text = "Image is not selected..."
                }

                it.exercises.map {
                    it.exercise.image = exerciseRepository.getExerciseImage(it.exercise)
                }

                exercises = it.exercises
            }

            setExerciseList()
        }


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
        findViewById<Button>(R.id.workout_config_btn_save).setOnClickListener {
            saveWorkout()
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

        setExerciseList()

        supportFragmentManager.findFragmentByTag("ExerciseDialog")?.let {
            (it as DialogFragment).dismiss()
        }
    }

    private fun setExerciseList() {
        if (::exerciseAdapter.isInitialized) {
            exerciseAdapter.notifyItemInserted(exercises.size - 1)
        } else {
            val recycler = findViewById<RecyclerView>(R.id.workout_config_exercises)
            recycler.layoutManager = LinearLayoutManager(this)
            exerciseAdapter = WorkoutExerciseAdapter(exercises, this, {}, {}, true)
            recycler.adapter = exerciseAdapter
        }
    }

    private fun saveWorkout() {
        val name = workoutNameField.text
        val description = findViewById<EditText>(R.id.workout_config_description).text

        if (name.isNullOrEmpty()) {
            toast("Name is required")
            return
        }
        if (description.isNullOrEmpty()) {
            toast("Description is required")
            return
        }
        if (exercises.isEmpty()) {
            toast("Exercise list is empty")
            return
        }

        val workout = WorkoutData(
            id = workout?.id ?: 0,
            name = name.toString(),
            description = description.toString(),
            image = imageUri?.toString(),
            isUserCreated = true,
            exercises = exercises
        )

        lifecycleScope.launch {
            if (workoutRepository.duplicateExists(workout)) {
                toast("Name is already taken")
                return@launch
            }

            workoutRepository.upsertWorkout(workout)
            finish()
        }
    }

    private fun toast(message: String, context: Context = this) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}