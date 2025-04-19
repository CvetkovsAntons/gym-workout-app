package com.example.gymworkoutapp.activities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ExerciseConfigActivity : AppCompatActivity() {

    private lateinit var repository: ExerciseRepository

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

                imageUri = uriToBase64(galleryUri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    )

    private var executionStepAdapter: TextListAdapter<ExerciseExecutionStep>? = null
    private var executionTipAdapter: TextListAdapter<ExerciseExecutionTip>? = null

    private var selectedMuscles = mutableListOf<Muscle>()
    private var selectedEquipment = mutableListOf<Equipment>()
    private var executionSteps = mutableListOf<ExerciseExecutionStep>()
    private var executionTips = mutableListOf<ExerciseExecutionTip>()

    private val difficulties = Difficulty.entries

    private lateinit var difficultySpinner: Spinner
    private var videoUrl: String? = null
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        repository = (application as App).exerciseRepository

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_config)

        lifecycleScope.launch {
            setMuscleList()
            setEquipmentList()
            setExecutionSteps()
            setExecutionTips()
            setDifficultySpinner()
            setVideo()
        }

        findViewById<EditText>(R.id.exercise_config_name).requestFocus()

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

    @SuppressLint("SetJavaScriptEnabled")
    private fun setVideo() {
        val video = findViewById<WebView>(R.id.exercise_config_video)
        video.settings.javaScriptEnabled = true

        videoUrl = findViewById<EditText>(R.id.exercise_config_video_url).text.toString().trim()
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
        setList(R.id.exercise_config_muscles, repository.getMuscleList(), selectedMuscles) {
            it.name
        }
    }

    private suspend fun setEquipmentList() {
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

        val index = difficulties.indexOf(Difficulty.BEGINNER)
        difficultySpinner.setSelection(index)
    }

    private fun saveExercise() {
        val name = findViewById<EditText>(R.id.exercise_config_name).text
        if (name.isNullOrEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return
        }

        val description = findViewById<EditText>(R.id.exercise_config_description).text
        if (description.isNullOrEmpty()) {
            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show()
            return
        }

        val difficulty = Difficulty.entries[difficultySpinner.selectedItemPosition]

        if (selectedMuscles.isEmpty()) {
            Toast.makeText(this, "Muscle is required", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedEquipment.isEmpty()) {
            Toast.makeText(this, "Equipment is required", Toast.LENGTH_SHORT).show()
            return
        }

        val steps = executionSteps.filter { it.description.isNotEmpty() }
        if (steps.isEmpty()) {
            Toast.makeText(this, "At least one execution step is required", Toast.LENGTH_SHORT).show()
            return
        }

        val tips = executionTips.filter { it.tip.isNotEmpty() }

        val exercise = ExerciseData(
            name = name.toString(),
            description = description.toString(),
            difficulty = difficulty,
            isUserCreated = true,
            isUserFavourite = false,
            equipment = selectedEquipment,
            muscles = selectedMuscles,
            image = imageUri,
            videoUrl = videoUrl,
            executionSteps = steps.toMutableList(),
            executionTips = tips.toMutableList(),
        )

        lifecycleScope.launch {
            repository.upsertExercise(exercise)
            finish()
        }
    }

    private fun uriToBase64(uri: Uri?): String? {
        if (uri == null) {
            return null
        }

        return try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}