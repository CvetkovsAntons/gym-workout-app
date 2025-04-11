package com.example.gymworkoutapp.api.services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("/auth/register")
    suspend fun register(@Body request: AuthRequest): Response<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<LoginResponse>

}

data class ErrorResponse(val error: String?)

data class AuthRequest(val email: String, val password: String, val passwordConfirm: String? = null)
data class RegisterResponse(val message: String?, val error: String?)
data class LoginResponse(val accessToken: String?, val refreshToken: String?, val error: String?)