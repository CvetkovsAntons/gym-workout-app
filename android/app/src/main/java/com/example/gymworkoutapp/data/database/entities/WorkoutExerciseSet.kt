package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "workout_exercise_set",
    primaryKeys = ["workout_exercise_id", "order_num"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExercise::class,
            parentColumns = ["id"],
            childColumns = ["workout_exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["workout_exercise_id", "order_num"], unique = true),
    ]
)
data class WorkoutExerciseSet(
    @ColumnInfo(name = "workout_exercise_id") val workoutExerciseId: Int,
    @ColumnInfo(name = "order_num") var orderNum: Int,
    @ColumnInfo(name = "reps") var reps: Int,
    @ColumnInfo(name = "weight") var weight: Float,
)
