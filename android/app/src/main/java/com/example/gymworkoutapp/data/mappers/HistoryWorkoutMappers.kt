package com.example.gymworkoutapp.data.mappers

import android.R.attr.id
import com.example.gymworkoutapp.data.database.entities.HistoryWorkout
import com.example.gymworkoutapp.data.database.entities.HistoryWorkoutExercise
import com.example.gymworkoutapp.data.database.entities.HistoryWorkoutExerciseSet
import com.example.gymworkoutapp.data.database.entities.Workout
import com.example.gymworkoutapp.data.database.entities.WorkoutExercise
import com.example.gymworkoutapp.data.database.entities.WorkoutExerciseSet
import com.example.gymworkoutapp.data.database.relations.HistoryWorkoutExerciseRelation
import com.example.gymworkoutapp.data.database.relations.HistoryWorkoutRelation
import com.example.gymworkoutapp.data.database.relations.WorkoutExerciseRelation
import com.example.gymworkoutapp.data.database.relations.WorkoutRelation
import com.example.gymworkoutapp.enums.WorkoutStatus
import com.example.gymworkoutapp.models.HistoryWorkoutData
import com.example.gymworkoutapp.models.WorkoutData
import com.example.gymworkoutapp.models.WorkoutExerciseData
import com.example.gymworkoutapp.models.WorkoutExerciseSetData
import java.sql.Timestamp

fun WorkoutData.toHistory(
    historyWorkoutId: Int? = null,
    startedAt: Timestamp,
    totalVolume: Float,
    status: WorkoutStatus = WorkoutStatus.IN_PROGRESS,
    finishedAt: Timestamp? = null
) = HistoryWorkoutData(
    id = historyWorkoutId ?: 0,
    workout = toEntity(),
    startedAt = startedAt,
    finishedAt = finishedAt,
    exercises = exercises,
    totalVolume = totalVolume,
    status = status
)

fun HistoryWorkoutRelation.toData() = HistoryWorkoutData(
    id = history.id,
    workout = Workout(
        id = workout.id,
        name = workout.name,
        description = workout.description,
        image = workout.image,
        isUserCreated = workout.isUserCreated
    ),
    startedAt = history.startedAt,
    finishedAt = history.finishedAt,
    totalVolume = history.totalVolume ?: 0f,
    status = history.status,
    exercises = exercises.map { it.toData() }.toMutableList()
)

fun HistoryWorkoutData.toEntity() = HistoryWorkout(
    id = id,
    workoutId = workout.id,
    startedAt = startedAt,
    finishedAt = finishedAt,
    totalVolume = totalVolume,
    status = status
)

fun HistoryWorkoutData.toWorkoutData() = WorkoutData(
    name = workout.name,
    description = workout.description,
    image = workout.image,
    isUserCreated = workout.isUserCreated,
    exercises = exercises
)

fun HistoryWorkoutExerciseRelation.toData() = WorkoutExerciseData(
    exercise = exerciseRelation.toData(),
    orderNum = workoutExercise.orderNum,
    sets = sets.map { it.toData() }.toMutableList()
)

fun WorkoutExerciseData.toHistoryEntity(workoutId: Int) = HistoryWorkoutExercise(
    workoutId = workoutId,
    exerciseId = exercise.id,
    orderNum = orderNum
)

fun HistoryWorkoutExerciseSet.toData() = WorkoutExerciseSetData(
    orderNum = orderNum,
    reps = reps,
    weight = weight,
    isLogged = isLogged
)

fun WorkoutExerciseSetData.toHistoryEntity(workoutExerciseId: Int) = HistoryWorkoutExerciseSet(
    workoutExerciseId = workoutExerciseId,
    orderNum = orderNum,
    reps = reps,
    weight = weight,
    isLogged = isLogged
)