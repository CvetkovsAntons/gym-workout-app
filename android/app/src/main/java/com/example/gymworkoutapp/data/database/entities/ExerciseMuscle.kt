package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "exercise_muscle",
    primaryKeys = ["exercise_id", "muscle_id"],
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Muscle::class,
            parentColumns = ["id"],
            childColumns = ["muscle_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["exercise_id"]),
        Index(value = ["muscle_id"])
    ]
)
data class ExerciseMuscle(
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "muscle_id") val muscleId: Int
)
