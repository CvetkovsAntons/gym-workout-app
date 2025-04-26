package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout",
    indices = [Index(value = ["name"], unique = true)]
)
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image") val image: String?,
    @ColumnInfo(name = "is_user_created") val isUserCreated: Boolean,
)
