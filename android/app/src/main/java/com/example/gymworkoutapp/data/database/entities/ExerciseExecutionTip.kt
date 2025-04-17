package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "exercise_execution_tip",
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
data class ExerciseExecutionTip(
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "order_num") val orderNum: Int,
    @ColumnInfo(name = "tip") val tip: String,
)
