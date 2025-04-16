package com.example.gymworkoutapp.auth

import android.annotation.SuppressLint
import android.content.Context
import com.example.gymworkoutapp.network.auth.TokenManager
import com.example.gymworkoutapp.network.client.ApiClient

object SessionManager {
    @SuppressLint("StaticFieldLeak")
    private lateinit var tokenManager: TokenManager

    fun init(context: Context) {
        tokenManager = TokenManager(context.applicationContext)
    }

    fun tokenManager(): TokenManager {
        return tokenManager
    }

    suspend fun isAuthenticated(): Boolean {
        var token = tokenManager.getAccessToken()
        if (token != null) {
            val response = ApiClient.userService.isAuthenticated()
            if (response.isSuccessful) {
                return true
            }
        }

        token = tokenManager.getRefreshToken()
        if (token == null) {
            return false
        }

        val response = ApiClient.authService.refresh("Bearer $token")
        if (!response.isSuccessful) {
            return false
        }

        val body = response.body()
        if (body == null || body.accessToken == null || body.refreshToken == null) {
            throw Exception("Invalid authentication response")
        }

        tokenManager.saveTokens(body.accessToken, body.refreshToken)

        return isAuthenticated()
    }
}