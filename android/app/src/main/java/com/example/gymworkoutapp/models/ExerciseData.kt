package com.example.gymworkoutapp.models

import android.os.Parcelable
import com.example.gymworkoutapp.data.database.entities.Equipment
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionStep
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionTip
import com.example.gymworkoutapp.data.database.entities.Muscle
import com.example.gymworkoutapp.enums.Difficulty
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExerciseData(
    val id: Int = 0,
    var name: String,
    var description: String,
    var videoUrl: String?,
    var image: String?,
    var difficulty: Difficulty,
    var isUserCreated: Boolean,
    var isUserFavourite: Boolean,
    var equipment: MutableList<Equipment>,
    var muscles: MutableList<Muscle>,
    var executionSteps: MutableList<ExerciseExecutionStep>,
    var executionTips: MutableList<ExerciseExecutionTip>
) : Parcelable
