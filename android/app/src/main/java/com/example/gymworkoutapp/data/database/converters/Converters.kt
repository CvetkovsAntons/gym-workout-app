package com.example.gymworkoutapp.data.database.converters

import androidx.room.TypeConverter
import com.example.gymworkoutapp.models.DateOfBirth

class Converters {
    @TypeConverter
    fun fromDateOfBirth(dateOfBirth: DateOfBirth): String {
        return "${dateOfBirth.day}-${dateOfBirth.month}-${dateOfBirth.year}"
    }

    @TypeConverter
    fun toDateOfBirth(dateOfBirth: String): DateOfBirth {
        val (day, month, year) = dateOfBirth.split("-").map { it.toInt() }
        return DateOfBirth(day, month, year)
    }
}