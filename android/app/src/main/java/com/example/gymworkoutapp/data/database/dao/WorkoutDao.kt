package com.example.gymworkoutapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import com.example.gymworkoutapp.data.database.entities.Workout
import com.example.gymworkoutapp.data.database.relations.WorkoutRelation

@Dao
interface WorkoutDao {
    @Transaction
    @Query("SELECT * FROM workout ORDER BY is_user_created, id DESC")
    suspend fun getAll(): List<WorkoutRelation>?

    @Transaction
    @Query("SELECT * FROM workout WHERE id = :id")
    suspend fun get(id: Int): WorkoutRelation?

    @Transaction
    @Delete
    suspend fun delete(exercise: Workout)

}