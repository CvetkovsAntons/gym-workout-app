package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "workout_exercise",
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["exercise_id"]),
        Index(value = ["workout_id"]),
        Index(value = ["order_num"]),
    ]
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "workout_id") val workoutId: Int,
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "order_num") val orderNum: Int,
)
