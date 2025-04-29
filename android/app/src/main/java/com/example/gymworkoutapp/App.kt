package com.example.gymworkoutapp

import android.app.Application
import com.example.gymworkoutapp.auth.SessionManager
import com.example.gymworkoutapp.data.database.AppDatabase
import com.example.gymworkoutapp.data.repository.ExerciseRepository
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.data.repository.WorkoutRepository
import com.example.gymworkoutapp.utils.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class App : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
        Helper.init(userRepository)

        appScope.launch {
            if (exerciseRepository.getMuscleList().isEmpty()) {
                exerciseRepository.insertDefaultMuscles()
            }
            if (exerciseRepository.getEquipmentList().isEmpty()) {
                exerciseRepository.insertDefaultEquipment()
            }
        }
    }

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    val exerciseRepository: ExerciseRepository by lazy {
        ExerciseRepository(database.exerciseDao())
    }

    val workoutRepository: WorkoutRepository by lazy {
        WorkoutRepository(database.workoutDao())
    }

}