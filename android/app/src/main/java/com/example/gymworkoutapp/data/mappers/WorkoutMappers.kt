package com.example.gymworkoutapp.data.mappers

import com.example.gymworkoutapp.data.database.entities.Workout
import com.example.gymworkoutapp.data.database.relations.WorkoutRelation
import com.example.gymworkoutapp.models.WorkoutData

fun WorkoutRelation.toWorkoutData() = WorkoutData(
    id = workout.id,
    name = workout.name,
    description = workout.description,
    image = workout.image,
    isUserCreated = workout.isUserCreated,
    exercises = workoutExercises.toMutableList()
)

fun WorkoutData.toWorkoutEntity() = Workout(
    id,
    name,
    description,
    image,
    isUserCreated
)