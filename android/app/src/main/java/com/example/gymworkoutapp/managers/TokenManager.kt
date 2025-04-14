package com.example.gymworkoutapp.managers

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(private val context: Context) {

    fun saveTokens(accessToken: String?, refreshToken: String?) {
        authPreferences().edit {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
        }
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

    private fun authPreferences(): SharedPreferences {
        return context.getSharedPreferences("auth_prefs", MODE_PRIVATE)
    }

}