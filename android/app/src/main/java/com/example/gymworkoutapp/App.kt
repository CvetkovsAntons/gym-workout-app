package com.example.gymworkoutapp

import android.app.Application
import com.example.gymworkoutapp.data.database.AppDatabase
import com.example.gymworkoutapp.data.repository.UserRepository

class App : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

}