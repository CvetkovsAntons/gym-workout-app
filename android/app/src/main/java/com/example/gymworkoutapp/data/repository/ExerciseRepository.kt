package com.example.gymworkoutapp.data.repository

import com.example.gymworkoutapp.data.database.dao.ExerciseDao
import com.example.gymworkoutapp.data.database.entities.ExerciseEquipment
import com.example.gymworkoutapp.data.database.entities.ExerciseMuscle
import com.example.gymworkoutapp.data.mappers.toExerciseData
import com.example.gymworkoutapp.data.mappers.toExerciseEntity
import com.example.gymworkoutapp.models.ExerciseData

class ExerciseRepository(private val exerciseDao: ExerciseDao) {
    suspend fun getExercise(): ExerciseData? {
        return exerciseDao.get()?.toExerciseData()
    }

    suspend fun upsertExercise(exerciseData: ExerciseData) {
        val exercise = exerciseData.toExerciseEntity()
        var exerciseId = exercise.id

        if (exercise.id == 0) {
            exerciseId = exerciseDao.insert(exercise).toInt()
        } else {
            exerciseDao.clearMuscles(exercise.id)
            exerciseDao.clearEquipment(exercise.id)
            exerciseDao.clearExecutionTips(exercise.id)
            exerciseDao.clearExecutionSteps(exercise.id)

            exerciseDao.update(exercise)
        }

        exerciseData.muscles.forEach { muscle ->
            exerciseDao.insert(
                ExerciseMuscle(exercise.id, muscle.id)
            )
        }

        exerciseData.equipment.forEach { equipment ->
            exerciseDao.insert(
                ExerciseEquipment(exercise.id, equipment.id)
            )
        }

        exerciseData.executionSteps.forEach { step ->
            exerciseDao.insert(step.copy(exerciseId = exerciseId))
        }

        exerciseData.executionTips.forEach { tip ->
            exerciseDao.insert(tip.copy(exerciseId = exerciseId))
        }
    }

}