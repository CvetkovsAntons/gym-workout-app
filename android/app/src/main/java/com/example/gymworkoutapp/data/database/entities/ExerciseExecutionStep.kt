package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "exercise_execution_step",
    primaryKeys = ["exercise_id", "order_num"],
    foreignKeys = [
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExerciseExecutionStep(
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "order_num") var orderNum: Int,
    @ColumnInfo(name = "description") var description: String,
)
