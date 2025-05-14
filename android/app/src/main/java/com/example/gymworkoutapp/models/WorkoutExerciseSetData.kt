package com.example.gymworkoutapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutExerciseSetData(
    var orderNum: Int,
    var reps: Int,
    var weight: Float,
    var isLogged: Boolean = false
) : Parcelable
