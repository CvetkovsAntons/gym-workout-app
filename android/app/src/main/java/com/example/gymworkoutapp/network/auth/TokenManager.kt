package com.example.gymworkoutapp.network.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.gymworkoutapp.models.ResponseAuth

class TokenManager(private val context: Context) {

    private fun authPreferences(): SharedPreferences {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    fun saveTokens(accessToken: String?, refreshToken: String?) {
        clearTokens()
        authPreferences().edit {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
        }
    }

    fun saveTokenFromResponse(response: ResponseAuth) {

    }

    fun getAccessToken(): String? {
        return authPreferences().getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return authPreferences().getString("refresh_token", null)
    }

    fun clearTokens() {
        authPreferences().edit {
            clear()
        }
    }

}