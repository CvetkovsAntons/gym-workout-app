package com.example.gymworkoutapp.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.gymworkoutapp.data.database.entities.HistoryWorkout
import com.example.gymworkoutapp.data.database.entities.Workout
import com.example.gymworkoutapp.data.database.entities.WorkoutExercise

data class HistoryWorkoutRelation(
    @Embedded val history: HistoryWorkout,

    @Relation(
        entity = Workout::class,
        parentColumn = "id",
        entityColumn = "workout_id"
    )
    val workout: Workout,

    @Relation(
        entity = WorkoutExercise::class,
        parentColumn = "id",
        entityColumn = "history_workout_id"
    )
    val exercises: List<HistoryWorkoutExerciseRelation>
)
