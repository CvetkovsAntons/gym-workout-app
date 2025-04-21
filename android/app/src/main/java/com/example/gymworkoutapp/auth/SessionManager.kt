package com.example.gymworkoutapp.auth

import android.annotation.SuppressLint
import android.content.Context
import com.example.gymworkoutapp.models.ResponseAuth
import com.example.gymworkoutapp.network.auth.TokenManager
import com.example.gymworkoutapp.network.client.ApiClient
import retrofit2.Response

object SessionManager {
    @SuppressLint("StaticFieldLeak")
    private lateinit var tokenManager: TokenManager

    fun init(context: Context) {
        tokenManager = TokenManager(context.applicationContext)
    }

    suspend fun isAuthenticated(): Boolean {
        if (ApiClient.userService.isAuthenticated().isSuccessful) {
            return true
        }

        val token = tokenManager.getRefreshToken(true)
        if (token.isNullOrEmpty()) {
            return false
        }

        val response = ApiClient.authService.refresh(token)
        if (!response.isSuccessful) {
            return false
        }

        tokenManager.saveTokensFromResponse(response)

        return isAuthenticated()
    }

    fun saveTokensFromResponse(response: Response<ResponseAuth>) {
        tokenManager.saveTokensFromResponse(response)
    }

    fun getAccessToken(formated: Boolean = false): String? {
        return tokenManager.getAccessToken(formated)
    }

    fun getRefreshToken(formated: Boolean = false): String? {
        return tokenManager.getRefreshToken(formated)
    }

    fun clearTokens() {
        tokenManager.clearTokens()
    }
}