package com.example.gymworkoutapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gymworkoutapp.data.database.entities.HistoryWorkout
import com.example.gymworkoutapp.data.database.entities.HistoryWorkoutExercise
import com.example.gymworkoutapp.data.database.entities.HistoryWorkoutExerciseSet
import com.example.gymworkoutapp.data.database.entities.Workout
import com.example.gymworkoutapp.data.database.entities.WorkoutExercise
import com.example.gymworkoutapp.data.database.entities.WorkoutExerciseSet
import com.example.gymworkoutapp.data.database.relations.HistoryWorkoutRelation
import com.example.gymworkoutapp.data.database.relations.WorkoutRelation
import com.example.gymworkoutapp.enums.WorkoutStatus

@Dao
interface WorkoutDao {
    @Transaction
    @Query("SELECT * FROM workout ORDER BY is_user_created, id DESC")
    suspend fun getAll(): List<WorkoutRelation>?

    @Transaction
    @Query("SELECT * FROM workout WHERE id = :id")
    suspend fun get(id: Int): WorkoutRelation?

    @Transaction
    @Query("SELECT * FROM workout WHERE id != :id AND name = :name")
    suspend fun searchForNameDuplicate(name: String, id: Int?): WorkoutRelation?

    @Transaction
    @Delete
    suspend fun delete(exercise: Workout)

    @Query("DELETE FROM workout_exercise WHERE workout_id = :workoutId")
    suspend fun clearWorkoutExercises(workoutId: Int)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workout: Workout): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workoutExercise: WorkoutExercise): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workoutExerciseSet: WorkoutExerciseSet): Long

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(workout: Workout)

    @Transaction
    @Query("SELECT image FROM workout WHERE id = :id")
    suspend fun getWorkoutImage(id: Int): String?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workout: HistoryWorkout): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workoutExercise: HistoryWorkoutExercise): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workoutExerciseSet: HistoryWorkoutExerciseSet): Long

    @Query("DELETE FROM history_workout_exercise WHERE history_workout_id = :id")
    suspend fun clearHistoryWorkoutExercises(id: Int)

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(workout: HistoryWorkout)

    @Transaction
    @Query("SELECT * FROM history_workout WHERE id = :id")
    suspend fun getHistoryWorkout(id: Int): HistoryWorkoutRelation?

    @Transaction
    @Query("SELECT * FROM history_workout WHERE workout_id = :workoutId AND status = :status")
    suspend fun getHistoryWorkout(workoutId: Int, status: WorkoutStatus): HistoryWorkoutRelation?

    @Transaction
    @Query("SELECT * FROM history_workout")
    suspend fun getHistory(): List<HistoryWorkoutRelation>?

    @Transaction
    @Query("SELECT count(id) FROM history_workout WHERE status = :status")
    suspend fun countOfHistoryWorkoutsByStatus(status: WorkoutStatus): Int

    @Transaction
    @Query("SELECT count(id) FROM history_workout")
    suspend fun countOfHistoryWorkouts(): Int

    @Transaction
    @Query("SELECT * FROM history_workout WHERE id = :id")
    suspend fun getHistoryWorkoutById(id: Int): HistoryWorkoutRelation?


}