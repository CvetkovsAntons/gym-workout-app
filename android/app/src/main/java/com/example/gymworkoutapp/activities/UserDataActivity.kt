package com.example.gymworkoutapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.models.UserData

class UserDataActivity() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)
    }

}