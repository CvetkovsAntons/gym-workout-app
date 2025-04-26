package com.example.gymworkoutapp.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.gymworkoutapp.data.database.entities.Exercise
import com.example.gymworkoutapp.data.database.entities.WorkoutExercise
import com.example.gymworkoutapp.data.database.entities.WorkoutExerciseSet

data class WorkoutExerciseRelation(
    @Embedded val workoutExercise: WorkoutExercise,

    @Relation(
        entity = Exercise::class,
        parentColumn = "exercise_id",
        entityColumn = "id"
    )
    val exerciseRelation: ExerciseRelation,

    @Relation(
        parentColumn = "id",
        entityColumn = "workout_exercise_id"
    )
    val sets: List<WorkoutExerciseSet>
)
