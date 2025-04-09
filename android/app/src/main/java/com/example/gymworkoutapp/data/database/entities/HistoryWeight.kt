package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "history_weight")
data class HistoryWeight(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "weight") val weight: Float,
    @ColumnInfo(name = "datetime") val datetime: Timestamp = Timestamp(System.currentTimeMillis())
)
