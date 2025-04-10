package com.example.gymworkoutapp.api

import com.example.gymworkoutapp.api.services.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "localhost"
    private const val AUTH_URL = "$BASE_URL:8080"

    val authService: AuthService = Retrofit.Builder()
        .baseUrl(AUTH_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AuthService::class.java)
}