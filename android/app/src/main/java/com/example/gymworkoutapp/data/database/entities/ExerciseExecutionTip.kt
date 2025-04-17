package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "exercise_execution_tip",
    primaryKeys = ["exercise_id", "order_num"]
)
data class ExerciseExecutionTip(
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "order_num") var orderNum: Int,
    @ColumnInfo(name = "tip") var tip: String,
)
