package com.example.gymworkoutapp.models

import com.google.gson.annotations.SerializedName

data class ResponseAuth(
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?, val
    error: String?
)
