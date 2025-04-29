package com.example.gymworkoutapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutExerciseData(
    val exercise: ExerciseData,
    var orderNum: Int,
    var sets: MutableList<WorkoutExerciseSetData>
) : Parcelable
