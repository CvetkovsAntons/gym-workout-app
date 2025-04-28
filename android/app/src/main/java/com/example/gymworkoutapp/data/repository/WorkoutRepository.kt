package com.example.gymworkoutapp.data.repository

import com.example.gymworkoutapp.data.database.dao.WorkoutDao
import com.example.gymworkoutapp.data.database.entities.ExerciseEquipment
import com.example.gymworkoutapp.data.database.entities.ExerciseMuscle
import com.example.gymworkoutapp.data.mappers.toData
import com.example.gymworkoutapp.data.mappers.toEntity
import com.example.gymworkoutapp.models.WorkoutData

class WorkoutRepository(private val dao: WorkoutDao) {

    suspend fun getAll(): MutableList<WorkoutData> {
        return dao.getAll()
            ?.map { it.toData() }
            ?.toMutableList()
            ?: mutableListOf()
    }

    suspend fun get(id: Int): WorkoutData? {
        return dao.get(id)?.toData()
    }

    suspend fun delete(exercise: WorkoutData) {
        dao.delete(exercise.toEntity())
    }

    suspend fun duplicateExists(workout: WorkoutData): Boolean {
        return dao.getByName(workout.name) != null
    }

    suspend fun upsertWorkout(workoutData: WorkoutData) {
        val workout = workoutData.toEntity()
        var workoutId = workout.id

        if (workout.id == 0) {
            workoutId = dao.insert(workout).toInt()
        } else {
            dao.clearWorkoutExercises(workout.id)
            dao.update(workout)
        }

        val workoutExercises = workoutData.exercises

        workoutExercises.forEach { exercise ->
            var workoutExerciseId = dao.insert(exercise.toEntity(workoutId)).toInt()

            exercise.sets.forEach { set ->
                dao.insert(set.toEntity(workoutExerciseId))
            }
        }
    }

}