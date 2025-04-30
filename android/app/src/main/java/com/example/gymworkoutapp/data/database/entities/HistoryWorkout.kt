package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gymworkoutapp.enums.WorkoutStatus
import java.sql.Timestamp

@Entity(
    tableName = "history_workout"
)
data class HistoryWorkout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "workout_id") val workoutId: Int,
    @ColumnInfo(name = "started_at") val startedAt: Timestamp = Timestamp(System.currentTimeMillis()),
    @ColumnInfo(name = "finished_at") var finishedAt: Timestamp? = null,
    @ColumnInfo(name = "total_volume") var totalVolume: Float? = 0f,
    @ColumnInfo(name = "status") var status: WorkoutStatus
)
