package com.example.gymworkoutapp.data.mappers

import com.example.gymworkoutapp.data.database.entities.Exercise
import com.example.gymworkoutapp.data.database.relations.ExerciseRelation
import com.example.gymworkoutapp.models.ExerciseData
import com.example.gymworkoutapp.models.WorkoutExerciseData
import com.example.gymworkoutapp.models.WorkoutExerciseSetData

fun ExerciseRelation.toData() = ExerciseData(
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

fun ExerciseData.toEntity() = Exercise(
    id,
    name,
    description,
    videoUrl,
    image,
    difficulty,
    isUserCreated,
    isUserFavourite
)

fun ExerciseData.toExerciseRelation() = ExerciseRelation(
    exercise = this.toEntity(),
    muscles = this.muscles,
    equipment = this.equipment,
    executionSteps = this.executionSteps,
    executionTips = this.executionTips
)

fun ExerciseData.toWorkoutExerciseData(orderNum: Int) = WorkoutExerciseData(
    exercise = this,
    orderNum = orderNum,
    sets = mutableListOf<WorkoutExerciseSetData>(WorkoutExerciseSetData(
        orderNum = 0,
        reps = 0,
        weight = 0.toFloat()
    ))
)