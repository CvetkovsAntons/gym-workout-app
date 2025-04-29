package com.example.gymworkoutapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Base64
import android.view.View
import android.webkit.WebView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.adapters.CheckBoxAdapter
import com.example.gymworkoutapp.adapters.TextListAdapter
import com.example.gymworkoutapp.data.database.entities.Equipment
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionStep
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionTip
import com.example.gymworkoutapp.data.database.entities.Muscle
import com.example.gymworkoutapp.data.repository.ExerciseRepository
import com.example.gymworkoutapp.enums.Difficulty
import com.example.gymworkoutapp.models.ExerciseData
import com.example.gymworkoutapp.utils.base64ToBitmap
import com.example.gymworkoutapp.utils.toBase64
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ExerciseConfigActivity : AppCompatActivity() {

    private lateinit var repository: ExerciseRepository

    private var exercise: ExerciseData? = null

    private var executionStepAdapter: TextListAdapter<ExerciseExecutionStep>? = null
    private var executionTipAdapter: TextListAdapter<ExerciseExecutionTip>? = null

    private var selectedMuscles = mutableListOf<Muscle>()
    private var selectedEquipment = mutableListOf<Equipment>()
    private var executionSteps = mutableListOf<ExerciseExecutionStep>()
    private var executionTips = mutableListOf<ExerciseExecutionTip>()

    private val difficulties = Difficulty.entries

    private lateinit var difficultySpinner: Spinner
    private lateinit var videoUrlInput: EditText
    private var videoUrl: String? = null
    private var imageUri: String? = null

    private lateinit var exerciseNameField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        repository = (application as App).exerciseRepository

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_config)

        exercise = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("exercise", ExerciseData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("exercise")
        }

        exerciseNameField = findViewById<EditText>(R.id.exercise_config_name)
        videoUrlInput = findViewById<EditText>(R.id.exercise_config_video_url)

        lifecycleScope.launch {
            exercise?.let {
                exerciseNameField.setText(it.name)
                findViewById<EditText>(R.id.exercise_config_description).setText(it.description)
                videoUrlInput.setText(it.videoUrl)

                imageUri = repository.getExerciseImage(it)
                val image = imageUri?.base64ToBitmap()
                val imageView = findViewById<ImageView>(R.id.exercise_config_image)
                val imageUriTextView = findViewById<TextView>(R.id.exercise_config_image_uri)

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
            }

            setMuscleList()
            setEquipmentList()
            setExecutionSteps()
            setExecutionTips()
            setDifficultySpinner()
            setVideo()
        }

        exerciseNameField.requestFocus()

        findViewById<Button>(R.id.exercise_config_btn_cancel).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.exercise_config_video_url_set_btn).setOnClickListener {
            setVideo()
        }
        findViewById<Button>(R.id.exercise_config_execution_steps_add_btn).setOnClickListener {
            addExecutionStep()
        }
        findViewById<Button>(R.id.exercise_config_execution_tips_add_btn).setOnClickListener {
            addExecutionTip()
        }
        findViewById<Button>(R.id.exercise_config_image_select_btn).setOnClickListener {
            galleryLauncher.launch("image/*")
        }
        findViewById<Button>(R.id.exercise_config_btn_save).setOnClickListener {
            saveExercise()
        }
    }

    val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent(),
        fun(it: Uri?) {
            val galleryUri = it
            val image = findViewById<ImageView>(R.id.exercise_config_image)
            val imageUriTextView = findViewById<TextView>(R.id.exercise_config_image_uri)

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

    @SuppressLint("SetJavaScriptEnabled")
    private fun setVideo() {
        val video = findViewById<WebView>(R.id.exercise_config_video)
        video.settings.javaScriptEnabled = true

        videoUrl = videoUrlInput.text.toString().trim()
        val regex = Regex("(?:youtu\\.be/|youtube\\.com(?:/embed/|/watch\\?v=|/v/|/shorts/))([\\w-]{11})")
        val match = regex.find(videoUrl.toString())
        val videoId = match?.groups?.get(1)?.value

        if (videoId != null) {
            val embedHtml = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/$videoId\" frameborder=\"0\" allowfullscreen></iframe>"
            video.loadData(embedHtml, "text/html", "utf-8")
            video.visibility = View.VISIBLE
        } else {
            video.visibility = View.GONE
        }
    }

    private suspend fun setMuscleList() {
        exercise?.let { selectedMuscles = it.muscles }

        setList(R.id.exercise_config_muscles, repository.getMuscleList(), selectedMuscles) {
            it.name
        }
    }

    private suspend fun setEquipmentList() {
        exercise?.let { selectedEquipment = it.equipment }

        setList(R.id.exercise_config_equipment, repository.getEquipmentList(), selectedEquipment) {
            it.name
        }
    }

    private fun <T> setList(
        recyclerId: Int,
        options: List<T>,
        selected: MutableList<T>,
        labelProvider: (T) -> String
    ) {
        val recycler = findViewById<RecyclerView>(recyclerId)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = CheckBoxAdapter(options, selected, labelProvider)
    }

    private fun addExecutionStep() {
        executionSteps.add(ExerciseExecutionStep(0, 0, ""))
        executionStepAdapter?.notifyItemInserted(executionSteps.lastIndex)
    }

    private fun addExecutionTip() {
        executionTips.add(ExerciseExecutionTip(0, 0, ""))
        executionTipAdapter?.notifyItemInserted(executionTips.lastIndex)
    }

    private fun setExecutionSteps() {
        exercise?.let { executionSteps = it.executionSteps }

        setExecutionStepsAndTips(
            R.id.exercise_config_execution_steps,
            executionSteps,
            { addExecutionStep() },
            { it.description },
            { item, newText -> item.description = newText },
            { adapter -> executionStepAdapter = adapter }
        )
    }

    private fun setExecutionTips() {
        exercise?.let { executionTips = it.executionTips }

        setExecutionStepsAndTips(
            R.id.exercise_config_execution_tips,
            executionTips,
            { addExecutionTip() },
            { it.tip },
            { item, newText -> item.tip = newText },
            { adapter -> executionTipAdapter = adapter }
        )
    }

    private fun <T> setExecutionStepsAndTips(
        recyclerId: Int,
        items: MutableList<T>,
        addRecord: () -> Unit,
        getText: (T) -> String,
        setText: (T, String) -> Unit,
        setAdapterRef: (TextListAdapter<T>) -> Unit
    ) {
        val recycler = findViewById<RecyclerView>(recyclerId)
        recycler.layoutManager = LinearLayoutManager(this)

        if (items.isEmpty()) {
            addRecord()
        }

        val adapter = TextListAdapter(items, getText, setText)
        setAdapterRef(adapter)
        recycler.adapter = adapter
    }

    private fun setDifficultySpinner() {
        difficultySpinner = findViewById<Spinner>(R.id.exercise_config_difficulty)

        val difficultyValues = difficulties.map { it.name }
        val adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            difficultyValues
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        difficultySpinner.adapter = adapter

        val index = exercise?.let { difficulties.indexOf(it.difficulty) }
            ?: difficulties.indexOf(Difficulty.BEGINNER)

        difficultySpinner.setSelection(index)
    }

    private fun saveExercise() {
        val name = exerciseNameField.text
        val steps = executionSteps.filter { it.description.isNotEmpty() }
        val description = findViewById<EditText>(R.id.exercise_config_description).text

        if (name.isNullOrEmpty()) {
            toast("Name is required")
            return
        }
        if (description.isNullOrEmpty()) {
            toast("Description is required")
            return
        }
        if (selectedMuscles.isEmpty()) {
            toast("Muscle is required")
            return
        }
        if (selectedEquipment.isEmpty()) {
            toast("Equipment is required")
            return
        }
        if (steps.isEmpty()) {
            toast("At least one execution step is required")
            return
        }

        val exercise = ExerciseData(
            id = exercise?.id ?: 0,
            name = name.toString(),
            description = description.toString(),
            difficulty = Difficulty.entries[difficultySpinner.selectedItemPosition],
            isUserCreated = true,
            isUserFavourite = false,
            equipment = selectedEquipment,
            muscles = selectedMuscles,
            image = imageUri,
            videoUrl = videoUrl,
            executionSteps = steps.toMutableList(),
            executionTips = (executionTips.filter { it.tip.isNotEmpty() }).toMutableList(),
        )

        lifecycleScope.launch {
            if (repository.duplicateExists(exercise)) {
                toast("Name or video URL is already taken")
                return@launch
            }

            repository.upsertExercise(exercise)
            finish()
        }
    }

    private fun toast(message: String, context: Context = this@ExerciseConfigActivity) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}