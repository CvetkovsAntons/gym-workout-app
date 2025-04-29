package com.example.gymworkoutapp.listeners

import com.example.gymworkoutapp.models.ExerciseData

interface OnExerciseSelectedListener {
    fun onExerciseSelected(exercise: ExerciseData)
}