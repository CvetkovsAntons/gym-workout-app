package com.example.gymworkoutapp.api

import com.example.gymworkoutapp.api.services.AuthService
import com.example.gymworkoutapp.api.services.UserService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://192.168.0.185"
    private const val AUTH_URL = "$BASE_URL:8080"

    val authService: AuthService = getApiClient().create(AuthService::class.java)

    val userService: UserService = getApiClient().create(UserService::class.java)

    private fun getApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(AUTH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

}