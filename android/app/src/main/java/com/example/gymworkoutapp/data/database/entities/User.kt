package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo(name = "weight") val weight: Float?,
    @ColumnInfo(name = "height") val height: Float?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "birth_day") val birthDay: Int?,
    @ColumnInfo(name = "birth_month") val birthMonth: Int?,
    @ColumnInfo(name = "birth_year") val birthYear: Int?,
)
