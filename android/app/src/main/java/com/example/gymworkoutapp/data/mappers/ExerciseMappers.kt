package com.example.gymworkoutapp.data.mappers

import com.example.gymworkoutapp.data.database.entities.Exercise
import com.example.gymworkoutapp.data.database.relations.ExerciseRelation
import com.example.gymworkoutapp.models.ExerciseData

fun ExerciseRelation.toExerciseData() = ExerciseData(
    id = exercise.id,
    name = exercise.name,
    description = exercise.description,
    videoUrl = exercise.videoUrl,
    image = exercise.image,
    difficulty = exercise.difficulty,
    isUserCreated = exercise.isUserCreated,
    isUserFavourite = exercise.isUserFavourite,
    equipment = equipment.toMutableList(),
    muscles = muscles.toMutableList(),
    executionSteps = executionSteps.toMutableList(),
    executionTips = executionTips.toMutableList()
)

fun ExerciseData.toExerciseEntity() = Exercise(
    id,
    name,
    description,
    videoUrl,
    image,
    difficulty,
    isUserCreated,
    isUserFavourite
)