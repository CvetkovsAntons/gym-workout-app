package com.example.gymworkoutapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gymworkoutapp.data.database.entities.Equipment
import com.example.gymworkoutapp.data.database.entities.Exercise
import com.example.gymworkoutapp.data.database.entities.ExerciseEquipment
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionStep
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionTip
import com.example.gymworkoutapp.data.database.entities.ExerciseMuscle
import com.example.gymworkoutapp.data.database.entities.Muscle
import com.example.gymworkoutapp.data.database.relations.ExerciseRelation

@Dao
interface ExerciseDao {
    @Transaction
    @Query("SELECT * FROM exercise ORDER BY is_user_created, id DESC")
    suspend fun getAll(): List<ExerciseRelation>?

    @Transaction
    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun get(id: Int): ExerciseRelation?

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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(muscle: Muscle)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(equipment: Equipment)

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

    @Query("SELECT * FROM muscle")
    suspend fun getMuscleList(): List<Muscle>

    @Query("SELECT * FROM equipment")
    suspend fun getEquipmentList(): List<Equipment>

}