package com.example.gymworkoutapp.data.mappers

import com.example.gymworkoutapp.data.database.entities.Workout
import com.example.gymworkoutapp.data.database.entities.WorkoutExercise
import com.example.gymworkoutapp.data.database.entities.WorkoutExerciseSet
import com.example.gymworkoutapp.data.database.relations.WorkoutExerciseRelation
import com.example.gymworkoutapp.data.database.relations.WorkoutRelation
import com.example.gymworkoutapp.models.WorkoutData
import com.example.gymworkoutapp.models.WorkoutExerciseData
import com.example.gymworkoutapp.models.WorkoutExerciseSetData

fun WorkoutRelation.toData() = WorkoutData(
    id = workout.id,
    name = workout.name,
    description = workout.description,
    image = workout.image,
    isUserCreated = workout.isUserCreated,
    exercises = workoutExercises.map { it.toData() }.toMutableList()
)

fun WorkoutData.toEntity() = Workout(
    id,
    name,
    description,
    image,
    isUserCreated
)

fun WorkoutExerciseRelation.toData() = WorkoutExerciseData(
    exercise = exerciseRelation.toData(),
    orderNum = workoutExercise.orderNum,
    sets = sets.map { it.toData() }.toMutableList()
)

fun WorkoutExerciseData.toEntity(workoutId: Int) = WorkoutExercise(
    workoutId = workoutId,
    exerciseId = exercise.id,
    orderNum = orderNum
)

fun WorkoutExerciseSet.toData() = WorkoutExerciseSetData(
    orderNum = orderNum,
    reps = reps,
    weight = weight
)

fun WorkoutExerciseSetData.toEntity(workoutExerciseId: Int) = WorkoutExerciseSet(
    workoutExerciseId = workoutExerciseId,
    orderNum = orderNum,
    reps = reps,
    weight = weight
)