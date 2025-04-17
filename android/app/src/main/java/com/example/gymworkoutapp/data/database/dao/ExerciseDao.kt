package com.example.gymworkoutapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gymworkoutapp.data.database.entities.Exercise
import com.example.gymworkoutapp.data.database.entities.ExerciseEquipment
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionStep
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionTip
import com.example.gymworkoutapp.data.database.entities.ExerciseMuscle
import com.example.gymworkoutapp.data.database.relations.ExerciseRelation

@Dao
interface ExerciseDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(equipment: ExerciseEquipment)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(muscle: ExerciseMuscle)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(executionTip: ExerciseExecutionTip)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(executionStep: ExerciseExecutionStep)

    @Transaction
    @Query("SELECT * FROM exercise LIMIT 1")
    suspend fun get(): ExerciseRelation?

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(exercise: Exercise)

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(equipment: ExerciseEquipment)

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(muscle: ExerciseMuscle)

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(executionTip: ExerciseExecutionTip)

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(executionStep: ExerciseExecutionStep)


    @Query("DELETE FROM exercise_muscle WHERE exercise_id = :exerciseId")
    suspend fun clearMuscles(exerciseId: Int)

    @Query("DELETE FROM exercise_equipment WHERE exercise_id = :exerciseId")
    suspend fun clearEquipment(exerciseId: Int)

    @Query("DELETE FROM exercise_execution_step WHERE exercise_id = :exerciseId")
    suspend fun clearExecutionSteps(exerciseId: Int)

    @Query("DELETE FROM exercise_execution_tip WHERE exercise_id = :exerciseId")
    suspend fun clearExecutionTips(exerciseId: Int)

}