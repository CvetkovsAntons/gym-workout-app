package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history_workout_exercise",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = HistoryWorkout::class,
            parentColumns = ["id"],
            childColumns = ["history_workout_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["exercise_id"]),
        Index(value = ["history_workout_id"]),
        Index(value = ["order_num"], unique = true),
    ]
)
data class HistoryWorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "history_workout_id") val workoutId: Int,
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "order_num") val orderNum: Int,
)
