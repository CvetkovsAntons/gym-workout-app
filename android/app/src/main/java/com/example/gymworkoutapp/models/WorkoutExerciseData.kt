package com.example.gymworkoutapp.models

data class WorkoutExerciseData(
    val exercise: ExerciseData,
    var orderNum: Int,
    var sets: MutableList<WorkoutExerciseSetData>
)
