package com.example.gymworkoutapp.network.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.gymworkoutapp.models.ResponseAuth
import retrofit2.Response

class TokenManager(private val context: Context) {

    enum class TokenType {
        ACCESS, REFRESH
    }

    private fun authPreferences(): SharedPreferences {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    private fun getToken(type: TokenType): String? {
        val tokenType = if (type == TokenType.ACCESS) "access_token" else "refresh_token"
        return authPreferences().getString(tokenType, null)
    }

    fun saveTokens(accessToken: String?, refreshToken: String?) {
        clearTokens()
        authPreferences().edit {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
        }
    }

    fun saveTokensFromResponse(response: Response<ResponseAuth>) {
        val body = response.body()
        if (body == null || body.accessToken == null || body.refreshToken == null) {
            throw Exception("Invalid authentication response")
        }
        saveTokens(body.accessToken, body.refreshToken)
    }

    fun getAccessToken(formated: Boolean = false): String? {
        val token = getToken(TokenType.ACCESS)
        if (token.isNullOrEmpty()) {
            return null
        }
        return if (formated) "Bearer $token" else token
    }

    fun getRefreshToken(formated: Boolean = false): String? {
        val token = getToken(TokenType.REFRESH)
        if (token.isNullOrEmpty()) {
            return null
        }
        return if (formated) "Bearer $token" else token
    }

    fun clearTokens() {
        authPreferences().edit { clear() }
    }

}