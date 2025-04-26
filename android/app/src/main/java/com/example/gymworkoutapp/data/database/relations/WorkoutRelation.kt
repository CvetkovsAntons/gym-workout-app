package com.example.gymworkoutapp.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.gymworkoutapp.data.database.entities.Workout
import com.example.gymworkoutapp.data.database.entities.WorkoutExercise

data class WorkoutRelation(
    @Embedded val workout: Workout,

    @Relation(
        entity = WorkoutExercise::class,
        parentColumn = "id",
        entityColumn = "workout_id"
    )
    val workoutExercises: List<WorkoutExerciseRelation>
)
