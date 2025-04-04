package com.example.gymworkoutapp

import android.app.Application
import com.example.gymworkoutapp.data.database.AppDatabase

class App : Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

}