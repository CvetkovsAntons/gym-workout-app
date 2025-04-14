package com.example.gymworkoutapp.api.services

import com.example.gymworkoutapp.models.RequestAuth
import com.example.gymworkoutapp.models.ResponseDefault
import com.example.gymworkoutapp.models.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserService {
    @Headers("Content-Type: application/json")
    @GET("/user/info")
    suspend fun getInfo(@Header("Authorization") accessToken: String): Response<UserData>

    @Headers("Content-Type: application/json")
    @POST("/user/info")
    suspend fun putInfo(
        @Header("Authorization") accessToken: String,
        @Body request: UserData
    ): Response<ResponseDefault>

}