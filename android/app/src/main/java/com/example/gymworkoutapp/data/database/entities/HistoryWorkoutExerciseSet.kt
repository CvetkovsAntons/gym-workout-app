package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "history_workout_exercise_set",
    primaryKeys = ["history_workout_exercise_id", "order_num"],
    foreignKeys = [
        ForeignKey(
            entity = HistoryWorkoutExercise::class,
            parentColumns = ["id"],
            childColumns = ["history_workout_exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["history_workout_exercise_id", "order_num"], unique = true),
    ]
)
data class HistoryWorkoutExerciseSet (
    @ColumnInfo(name = "history_workout_exercise_id") val workoutExerciseId: Int,
    @ColumnInfo(name = "order_num") var orderNum: Int,
    @ColumnInfo(name = "reps") var reps: Int,
    @ColumnInfo(name = "weight") var weight: Float,
    @ColumnInfo(name = "is_logged") var isLogged: Boolean = false
)