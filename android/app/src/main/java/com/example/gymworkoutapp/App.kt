package com.example.gymworkoutapp

import android.app.Application
import com.example.gymworkoutapp.auth.SessionManager
import com.example.gymworkoutapp.data.database.AppDatabase
import com.example.gymworkoutapp.data.repository.ExerciseRepository
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.utils.Helper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
        Helper.init(userRepository)
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

}