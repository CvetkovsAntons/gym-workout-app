package com.example.gymworkoutapp.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gymworkoutapp.enums.Difficulty

@Entity(
    tableName = "exercise",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["video_url"], unique = true),
    ]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "video_url") val videoUrl: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "difficulty") val difficulty: Difficulty,
    @ColumnInfo(name = "is_user_created") val isUserCreated: Boolean,
    @ColumnInfo(name = "is_user_favourite") val isUserFavourite: Boolean,
)
