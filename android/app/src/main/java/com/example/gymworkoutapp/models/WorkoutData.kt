package com.example.gymworkoutapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutData(
    var id: Int = 0,
    var name: String,
    var description: String,
    var image: String?,
    var isUserCreated: Boolean,
    var exercises: MutableList<WorkoutExerciseData>
) : Parcelable
