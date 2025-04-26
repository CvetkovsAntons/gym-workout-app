package com.example.gymworkoutapp.models

import com.example.gymworkoutapp.data.database.relations.WorkoutExerciseRelation

data class WorkoutData(
    var id: Int = 0,
    var name: String,
    var description: String,
    var image: String?,
    var isUserCreated: Boolean,
    var exercises: MutableList<WorkoutExerciseRelation>
)
