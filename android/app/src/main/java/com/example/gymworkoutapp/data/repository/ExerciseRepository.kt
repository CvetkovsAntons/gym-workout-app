package com.example.gymworkoutapp.data.repository

import android.util.Log
import com.example.gymworkoutapp.data.database.dao.ExerciseDao
import com.example.gymworkoutapp.data.database.entities.Equipment
import com.example.gymworkoutapp.data.database.entities.ExerciseEquipment
import com.example.gymworkoutapp.data.database.entities.ExerciseMuscle
import com.example.gymworkoutapp.data.database.entities.Muscle
import com.example.gymworkoutapp.data.mappers.toExerciseData
import com.example.gymworkoutapp.data.mappers.toExerciseEntity
import com.example.gymworkoutapp.models.ExerciseData

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    suspend fun getAllExercises(): MutableList<ExerciseData> {
        return exerciseDao.getAll()
            ?.map { it.toExerciseData() }
            ?.toMutableList()
            ?: mutableListOf()
    }

    suspend fun getExercise(id: Int): ExerciseData? {
        return exerciseDao.get(id)?.toExerciseData()
    }

    suspend fun upsertExercise(exerciseData: ExerciseData) {
        val exercise = exerciseData.toExerciseEntity()
        var exerciseId = exercise.id
        Log.d("exercise_id", exerciseId.toString())
        Log.d("exercise_id", exerciseData.toString())
        Log.d("exercise_id", exercise.toString())
        if (exercise.id == 0) {
            exerciseId = exerciseDao.insert(exercise).toInt()
        } else {
            exerciseDao.clearMuscles(exercise.id)
            exerciseDao.clearEquipment(exercise.id)
            exerciseDao.clearExecutionTips(exercise.id)
            exerciseDao.clearExecutionSteps(exercise.id)

            exerciseDao.update(exercise)
        }
        Log.d("exercise_id", exerciseId.toString())

        exerciseData.muscles.forEach { muscle ->
            exerciseDao.insert(ExerciseMuscle(exerciseId, muscle.id))
        }

        exerciseData.equipment.forEach { equipment ->
            exerciseDao.insert(ExerciseEquipment(exerciseId, equipment.id))
        }

        exerciseData.executionSteps.forEach { step ->
            exerciseDao.insert(step.copy(exerciseId = exerciseId))
        }

        exerciseData.executionTips.forEach { tip ->
            exerciseDao.insert(tip.copy(exerciseId = exerciseId))
        }
    }

    suspend fun insertDefaultMuscles() {
        val defaultMuscles = listOf(
            Muscle(id = 0, name = "Chest"),
            Muscle(id = 0, name = "Back"),
            Muscle(id = 0, name = "Shoulders"),
            Muscle(id = 0, name = "Abs"),
            Muscle(id = 0, name = "Biceps"),
            Muscle(id = 0, name = "Triceps"),
            Muscle(id = 0, name = "Quads"),
            Muscle(id = 0, name = "Hamstrings"),
            Muscle(id = 0, name = "Calves"),
            Muscle(id = 0, name = "Glutes"),
        )
        defaultMuscles.forEach { exerciseDao.insert(it) }
    }

    suspend fun getMuscleList(): List<Muscle> {
        return exerciseDao.getMuscleList()
    }

    suspend fun insertDefaultEquipment() {
        val defaultMuscles = listOf(
            Equipment(id = 0, name = "Bench"),
            Equipment(id = 0, name = "Flat bench"),
            Equipment(id = 0, name = "Dumbbell"),
            Equipment(id = 0, name = "Bar"),
            Equipment(id = 0, name = "Hex-bar"),
            Equipment(id = 0, name = "Machine"),
            Equipment(id = 0, name = "Kettlebell"),
            Equipment(id = 0, name = "Resisting band"),
            Equipment(id = 0, name = "Medicine ball"),
            Equipment(id = 0, name = "Pull-up bar"),
            Equipment(id = 0, name = "Dip bar"),
            Equipment(id = 0, name = "Rings"),
        )
        defaultMuscles.forEach { exerciseDao.insert(it) }
    }

    suspend fun getEquipmentList(): List<Equipment> {
        return exerciseDao.getEquipmentList()
    }

}