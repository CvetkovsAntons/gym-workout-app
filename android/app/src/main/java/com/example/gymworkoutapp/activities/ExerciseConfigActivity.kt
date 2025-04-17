package com.example.gymworkoutapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.adapters.CheckBoxAdapter
import com.example.gymworkoutapp.data.database.entities.Equipment
import com.example.gymworkoutapp.data.database.entities.Muscle
import com.example.gymworkoutapp.data.repository.ExerciseRepository
import kotlinx.coroutines.launch

class ExerciseConfigActivity : AppCompatActivity() {

    private lateinit var repository: ExerciseRepository

    private var selectedMuscles = mutableListOf<Muscle>()
    private var selectedEquipment = mutableListOf<Equipment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        repository = (application as App).exerciseRepository

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_config)

        prepareLayout()

        findViewById<EditText>(R.id.exercise_config_name).requestFocus()

        findViewById<Button>(R.id.exercise_config_btn_cancel).setOnClickListener {
            finish()
        }
    }

    private fun prepareLayout() {
        lifecycleScope.launch {
            setMuscleList()
            setEquipmentList()
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

}