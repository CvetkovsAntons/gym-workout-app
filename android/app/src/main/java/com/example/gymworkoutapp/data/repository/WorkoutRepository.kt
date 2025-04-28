package com.example.gymworkoutapp.data.repository

import com.example.gymworkoutapp.data.database.dao.WorkoutDao
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

}