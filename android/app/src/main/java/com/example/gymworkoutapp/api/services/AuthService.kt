package com.example.gymworkoutapp.api.services

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/auth/register")
    suspend fun register(@Body request: AuthRequest): AuthResponse

    @POST("/auth/login")
    suspend fun login(@Body request: AuthRequest): AuthResponse

}

data class AuthRequest(val email: String, val password: String, val resetCode: String? = null)
data class AuthResponse(val accessToken: String, val refreshToken: String)