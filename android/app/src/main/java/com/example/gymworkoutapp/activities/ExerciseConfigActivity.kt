package com.example.gymworkoutapp.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
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
import kotlinx.coroutines.launch

class ExerciseConfigActivity : AppCompatActivity() {

    private lateinit var repository: ExerciseRepository

//    val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent(),
//        fun(it: Uri?) {
//            val galleryUri = it
//            try {
//                findViewById<ImageView>(R.id.exercise_config_image).setImageURI(galleryUri)
//                findViewById<Button>(R.id.exercise_config_image_btn).visibility = View.GONE
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    )

    private var executionStepAdapter: TextListAdapter<ExerciseExecutionStep>? = null
    private var executionTipAdapter: TextListAdapter<ExerciseExecutionTip>? = null

    private var selectedMuscles = mutableListOf<Muscle>()
    private var selectedEquipment = mutableListOf<Equipment>()
    private var executionSteps = mutableListOf<ExerciseExecutionStep>()
    private var executionTips = mutableListOf<ExerciseExecutionTip>()

    override fun onCreate(savedInstanceState: Bundle?) {
        repository = (application as App).exerciseRepository

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_config)

        prepareLayout()

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
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setVideo() {
        val video = findViewById<WebView>(R.id.exercise_config_video)
        video.settings.javaScriptEnabled = true

        val url = findViewById<EditText>(R.id.exercise_config_video_url).text.toString().trim()
        val regex = Regex("(?:youtu\\.be/|youtube\\.com(?:/embed/|/watch\\?v=|/v/|/shorts/))([\\w-]{11})")
        val match = regex.find(url)
        val videoId = match?.groups?.get(1)?.value

        if (videoId != null) {
            val embedHtml = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/$videoId\" frameborder=\"0\" allowfullscreen></iframe>"
            video.loadData(embedHtml, "text/html", "utf-8")
            video.visibility = View.VISIBLE
        } else {
            video.visibility = View.GONE
        }
    }

    private fun prepareLayout() {
        lifecycleScope.launch {
            setMuscleList()
            setEquipmentList()
            setExecutionSteps()
            setExecutionTips()
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

    private fun addExecutionStep() {
        executionSteps.add(ExerciseExecutionStep(0, 0, ""))
        executionStepAdapter?.notifyItemInserted(executionSteps.lastIndex)
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

    private fun addExecutionTip() {
        executionTips.add(ExerciseExecutionTip(0, 0, ""))
        executionTipAdapter?.notifyItemInserted(executionTips.lastIndex)
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

}