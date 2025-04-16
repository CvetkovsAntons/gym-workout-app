package com.example.gymworkoutapp.network.services

import com.example.gymworkoutapp.models.ResponseDefault
import com.example.gymworkoutapp.models.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PUT

interface UserService {
    @Headers("Content-Type: application/json")
    @GET("/user/isAuthenticated")
    suspend fun isAuthenticated(): Response<ResponseDefault>

    @Headers("Content-Type: application/json")
    @PUT("/auth/logout")
    suspend fun logout(): Response<ResponseDefault>

    @Headers("Content-Type: application/json")
    @DELETE("/user/delete")
    suspend fun delete(): Response<ResponseDefault>

    @Headers("Content-Type: application/json")
    @GET("/user/info")
    suspend fun getInfo(): Response<UserData>

    @Headers("Content-Type: application/json")
    @PUT("/user/info")
    suspend fun putInfo(@Body request: UserData): Response<ResponseDefault>
}