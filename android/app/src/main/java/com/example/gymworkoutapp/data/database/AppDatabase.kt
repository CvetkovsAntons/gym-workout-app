package com.example.gymworkoutapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gymworkoutapp.data.database.converters.Converters
import com.example.gymworkoutapp.data.database.dao.ExerciseDao
import com.example.gymworkoutapp.data.database.dao.UserDao
import com.example.gymworkoutapp.data.database.dao.WorkoutDao
import com.example.gymworkoutapp.data.database.entities.Equipment
import com.example.gymworkoutapp.data.database.entities.Exercise
import com.example.gymworkoutapp.data.database.entities.ExerciseEquipment
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionStep
import com.example.gymworkoutapp.data.database.entities.ExerciseExecutionTip
import com.example.gymworkoutapp.data.database.entities.ExerciseMuscle
import com.example.gymworkoutapp.data.database.entities.HistoryWeight
import com.example.gymworkoutapp.data.database.entities.HistoryWorkout
import com.example.gymworkoutapp.data.database.entities.HistoryWorkoutExercise
import com.example.gymworkoutapp.data.database.entities.HistoryWorkoutExerciseSet
import com.example.gymworkoutapp.data.database.entities.Muscle
import com.example.gymworkoutapp.data.database.entities.User
import com.example.gymworkoutapp.data.database.entities.Workout
import com.example.gymworkoutapp.data.database.entities.WorkoutExercise
import com.example.gymworkoutapp.data.database.entities.WorkoutExerciseSet

@Database(
    entities = [
        User::class, HistoryWeight::class, Muscle::class, Equipment::class,
        Exercise::class, ExerciseMuscle::class, ExerciseEquipment::class,
        ExerciseExecutionTip::class, ExerciseExecutionStep::class, Workout::class,
        WorkoutExercise::class, WorkoutExerciseSet::class, HistoryWorkout::class,
        HistoryWorkoutExercise::class, HistoryWorkoutExerciseSet::class
    ],
    version = 17
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gym_workout_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}