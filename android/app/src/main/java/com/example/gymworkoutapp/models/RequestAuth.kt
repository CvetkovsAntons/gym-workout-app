package com.example.gymworkoutapp.models

import com.google.gson.annotations.SerializedName

data class RequestAuth(
    val email: String,
    val password: String,
    @SerializedName("password_confirm")
    val passwordConfirm: String? = null
)
