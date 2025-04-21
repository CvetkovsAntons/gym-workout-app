package com.example.gymworkoutapp.data.database.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.gymworkoutapp.data.database.entities.Equipment
import com.example.gymworkoutapp.data.database.entities.Exercise
import com.example.gymworkoutapp.data.database.entities.ExerciseEquipment
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionStep
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionTip
import com.example.gymworkoutapp.data.database.entities.ExerciseMuscle
import com.example.gymworkoutapp.data.database.entities.Muscle

data class ExerciseRelation(
    @Embedded val exercise: Exercise,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ExerciseMuscle::class,
            parentColumn = "exercise_id",
            entityColumn = "muscle_id"
        )
    )
    val muscles: List<Muscle>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ExerciseEquipment::class,
            parentColumn = "exercise_id",
            entityColumn = "equipment_id"
        )
    )
    val equipment: List<Equipment>,

    @Relation(
        parentColumn = "id",
        entityColumn = "exercise_id"
    )
    val executionSteps: List<ExerciseExecutionStep>,

    @Relation(
        parentColumn = "id",
        entityColumn = "exercise_id"
    )
    val executionTips: List<ExerciseExecutionTip>
)
