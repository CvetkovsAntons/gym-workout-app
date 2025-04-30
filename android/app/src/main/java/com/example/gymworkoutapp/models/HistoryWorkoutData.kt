package com.example.gymworkoutapp.models

import com.example.gymworkoutapp.data.database.entities.Workout
import com.example.gymworkoutapp.enums.WorkoutStatus
import java.sql.Timestamp

data class HistoryWorkoutData(
    val id: Int = 0,
    val workout: Workout,
    var startedAt: Timestamp = Timestamp(System.currentTimeMillis()),
    var finishedAt: Timestamp? = null,
    var totalVolume: Float? = 0f,
    var status: WorkoutStatus = WorkoutStatus.IN_PROGRESS,
    var exercises: MutableList<WorkoutExerciseData>
)
