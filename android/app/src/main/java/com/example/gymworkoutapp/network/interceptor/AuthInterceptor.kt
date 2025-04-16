package com.example.gymworkoutapp.network.interceptor

import com.example.gymworkoutapp.auth.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = SessionManager.getAccessToken()

        val newRequest = chain.request().newBuilder().apply {
            if (!accessToken.isNullOrEmpty()) {
                header("Authorization", "Bearer $accessToken")
            }
        }.build()

        return chain.proceed(newRequest)
    }
}