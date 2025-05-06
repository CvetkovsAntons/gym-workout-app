package com.example.gymworkoutapp.data.repository

import com.example.gymworkoutapp.data.database.dao.WorkoutDao
import com.example.gymworkoutapp.data.mappers.toData
import com.example.gymworkoutapp.data.mappers.toEntity
import com.example.gymworkoutapp.data.mappers.toHistoryEntity
import com.example.gymworkoutapp.enums.WorkoutStatus
import com.example.gymworkoutapp.models.HistoryWorkoutData
import com.example.gymworkoutapp.models.WorkoutData

class WorkoutRepository(private val dao: WorkoutDao) {

    suspend fun getAllWorkouts(): MutableList<WorkoutData> {
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
        return dao.searchForNameDuplicate(workout.name, workout.id) != null
    }

    suspend fun upsertWorkout(workoutData: WorkoutData) {
        val workout = workoutData.toEntity()
        var workoutId = workout.id

        if (workout.id == 0) {
            workoutId = dao.insert(workout).toInt()
        } else {
            dao.clearWorkoutExercises(workoutId)
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

    suspend fun getWorkoutImage(workout: WorkoutData): String? {
        if (workout.id == 0) {
            return null
        }
        return dao.getWorkoutImage(workout.id)
    }

    suspend fun getHistoryWorkout(id: Int): HistoryWorkoutData?  {
        return dao.getHistoryWorkout(id)?.toData()
    }

    suspend fun getHistoryWorkout(workoutId: Int, status: WorkoutStatus): HistoryWorkoutData?  {
        return dao.getHistoryWorkout(workoutId, status)?.toData()
    }

    suspend fun getHistory(): List<HistoryWorkoutData>?  {
        return dao.getHistory()?.map { it.toData() }
    }

    suspend fun countOfFinishedWorkouts(): Int  {
        return dao.countOfHistoryWorkoutsByStatus(WorkoutStatus.FINISHED)
    }

    suspend fun countOfStartedWorkouts(): Int  {
        return dao.countOfHistoryWorkouts()
    }

    suspend fun upsertHistoryWorkout(historyWorkoutData: HistoryWorkoutData): HistoryWorkoutData? {
        val workout = historyWorkoutData.toEntity()
        var historyWorkoutId = workout.id

        if (workout.id == 0) {
            historyWorkoutId = dao.insert(workout).toInt()
        } else {
            dao.clearHistoryWorkoutExercises(historyWorkoutId)
            dao.update(workout)
        }

        val exercises = historyWorkoutData.exercises

        exercises.forEach { exercise ->
            var workoutExerciseId = dao.insert(exercise.toHistoryEntity(historyWorkoutId)).toInt()

            exercise.sets.forEach { set ->
                dao.insert(set.toHistoryEntity(workoutExerciseId))
            }
        }

        return dao.getHistoryWorkoutById(historyWorkoutId)?.toData()
    }

}