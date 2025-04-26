package com.example.gymworkoutapp.data.repository

import com.example.gymworkoutapp.data.database.dao.WorkoutDao
import com.example.gymworkoutapp.data.mappers.toWorkoutData
import com.example.gymworkoutapp.data.mappers.toWorkoutEntity
import com.example.gymworkoutapp.models.WorkoutData

class WorkoutRepository(private val dao: WorkoutDao) {

    suspend fun getAll(): MutableList<WorkoutData> {
        return dao.getAll()
            ?.map { it.toWorkoutData() }
            ?.toMutableList()
            ?: mutableListOf()
    }

    suspend fun get(id: Int): WorkoutData? {
        return dao.get(id)?.toWorkoutData()
    }

    suspend fun delete(exercise: WorkoutData) {
        dao.delete(exercise.toWorkoutEntity())
    }

}