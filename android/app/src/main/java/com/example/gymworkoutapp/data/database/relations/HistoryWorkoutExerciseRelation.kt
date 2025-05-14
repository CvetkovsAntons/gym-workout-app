package com.example.gymworkoutapp.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.gymworkoutapp.data.database.entities.Exercise
import com.example.gymworkoutapp.data.database.entities.HistoryWorkoutExercise
import com.example.gymworkoutapp.data.database.entities.HistoryWorkoutExerciseSet
import com.example.gymworkoutapp.data.database.entities.WorkoutExercise
import com.example.gymworkoutapp.data.database.entities.WorkoutExerciseSet

data class HistoryWorkoutExerciseRelation(
    @Embedded val workoutExercise: HistoryWorkoutExercise,

    @Relation(
        entity = Exercise::class,
        parentColumn = "exercise_id",
        entityColumn = "id"
    )
    val exerciseRelation: ExerciseRelation,

    @Relation(
        parentColumn = "id",
        entityColumn = "history_workout_exercise_id"
    )
    val sets: List<HistoryWorkoutExerciseSet>
)
