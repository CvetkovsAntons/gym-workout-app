package com.example.gymworkoutapp.data.repository

import com.example.gymworkoutapp.data.database.dao.ExerciseDao
import com.example.gymworkoutapp.data.database.entities.Equipment
import com.example.gymworkoutapp.data.database.entities.ExerciseEquipment
import com.example.gymworkoutapp.data.database.entities.ExerciseMuscle
import com.example.gymworkoutapp.data.database.entities.Muscle
import com.example.gymworkoutapp.data.mappers.toExerciseData
import com.example.gymworkoutapp.data.mappers.toExerciseEntity
import com.example.gymworkoutapp.models.ExerciseData

class ExerciseRepository(private val dao: ExerciseDao) {

    suspend fun getAllExercises(): MutableList<ExerciseData> {
        return dao.getAll()
            ?.map { it.toExerciseData() }
            ?.toMutableList()
            ?: mutableListOf()
    }

    suspend fun getExercise(id: Int): ExerciseData? {
        return dao.get(id)?.toExerciseData()
    }

    suspend fun upsertExercise(exerciseData: ExerciseData) {
        val exercise = exerciseData.toExerciseEntity()
        var exerciseId = exercise.id

        if (exercise.id == 0) {
            exerciseId = dao.insert(exercise).toInt()
        } else {
            dao.clearMuscles(exercise.id)
            dao.clearEquipment(exercise.id)
            dao.clearExecutionTips(exercise.id)
            dao.clearExecutionSteps(exercise.id)

            dao.update(exercise)
        }

        exerciseData.muscles.forEach { muscle ->
            dao.insert(ExerciseMuscle(exerciseId, muscle.id))
        }
        exerciseData.equipment.forEach { equipment ->
            dao.insert(ExerciseEquipment(exerciseId, equipment.id))
        }
        exerciseData.executionSteps.forEach { step ->
            dao.insert(step.copy(exerciseId = exerciseId))
        }
        exerciseData.executionTips.forEach { tip ->
            dao.insert(tip.copy(exerciseId = exerciseId))
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
        defaultMuscles.forEach { dao.insert(it) }
    }

    suspend fun getMuscleList(): List<Muscle> {
        return dao.getMuscleList()
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
        defaultMuscles.forEach { dao.insert(it) }
    }

    suspend fun getEquipmentList(): List<Equipment> {
        return dao.getEquipmentList()
    }

    suspend fun duplicateExists(exercise: ExerciseData): Boolean {
        return dao.getByNameAndVideoUrl(exercise.name, exercise.id) != null
    }

    suspend fun deleteExercise(exercise: ExerciseData) {
        dao.delete(exercise.toExerciseEntity())
    }

    suspend fun getExerciseImage(exercise: ExerciseData): String? {
        if (exercise.id == 0) {
            return null
        }
        return dao.getExerciseImage(exercise.id)
    }

}