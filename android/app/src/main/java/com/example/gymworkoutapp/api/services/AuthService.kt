package com.example.gymworkoutapp.api.services

import com.example.gymworkoutapp.models.RequestAuth
import com.example.gymworkoutapp.models.ResponseDefault
import com.example.gymworkoutapp.models.ResponseAuth
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("/auth/register")
    suspend fun register(@Body request: RequestAuth): Response<ResponseDefault>

    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    suspend fun login(@Body request: RequestAuth): Response<ResponseAuth>

    @Headers("Content-Type: application/json")
    @POST("/auth/refresh")
    suspend fun refresh(
        @Header("Authorization") refreshToken: String,
        @Body request: RequestAuth
    ): Response<ResponseAuth>

}
