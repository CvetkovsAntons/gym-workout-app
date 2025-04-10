package com.example.gymworkoutapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gymworkoutapp.App
import com.example.gymworkoutapp.R
import com.example.gymworkoutapp.data.repository.UserRepository
import com.example.gymworkoutapp.models.DateOfBirth
import com.example.gymworkoutapp.models.UserData
import kotlinx.coroutines.launch

class UserDataActivity : AppCompatActivity() {

    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        val rootView = findViewById<View>(R.id.user_data)
        rootView.visibility = View.GONE

        val cancelButton = rootView.findViewById<Button>(R.id.profile_edit_cancel)

        repository = (application as App).userRepository

        lifecycleScope.launch {
            val userData = repository.getUserData()

            if (userData != null) {
                cancelButton.visibility = View.VISIBLE

                setValue(R.id.profile_edit_name, userData.name.toString())
                setValue(R.id.profile_edit_height, userData.height.toString())
                setValue(R.id.profile_edit_weight, userData.weight.toString())

                if (userData.dateOfBirth != null) {
                    val dob = userData.dateOfBirth
                    setValue(R.id.profile_edit_day, dob.day.toString())
                    setValue(R.id.profile_edit_month, dob.month.toString())
                    setValue(R.id.profile_edit_year, dob.year.toString())
                }
            }

            rootView.visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.profile_edit_save).setOnClickListener {
            saveUserData()
        }

        cancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun saveUserData() {
        val name = getValue(R.id.profile_edit_name).trim()
        val height = getValue(R.id.profile_edit_height).toFloatOrNull()
        val weight = getValue(R.id.profile_edit_weight).toFloatOrNull()
        val day = getValue(R.id.profile_edit_day).toIntOrNull()
        val month = getValue(R.id.profile_edit_month).toIntOrNull()
        val year = getValue(R.id.profile_edit_year).toIntOrNull()

        if (name.isEmpty() || height == null || weight == null || day == null || month == null || year == null) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val newUserData = UserData(
            name = name,
            height = height,
            weight = weight,
            dateOfBirth = DateOfBirth(day, month, year)
        )

        lifecycleScope.launch {
            val oldUserData = repository.getUserData()

            if (oldUserData == null) {
                repository.insertUserData(newUserData)
            } else {
                repository.updateUserData(newUserData)
            }

            if (newUserData.weight != null && oldUserData?.weight != newUserData.weight) {
                repository.insertHistoryWeight(newUserData.weight)
            }

            setResult(RESULT_OK)
            finish()
        }
    }

    private fun setValue(id: Int, value: String) {
        getEditText(id).setText(value)
    }

    private fun getValue(id: Int): String {
        return getEditText(id).text.toString()
    }

    private fun getEditText(id: Int): EditText {
        return findViewById(id)
    }

}