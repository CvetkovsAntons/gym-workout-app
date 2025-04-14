package com.example.gymworkoutapp.models

import com.google.gson.annotations.SerializedName

data class UserData(
    val weight: Float?,
    val height: Float?,
    val name: String?,
    @SerializedName("date_of_birth")
    val dateOfBirth: DateOfBirth?,
)