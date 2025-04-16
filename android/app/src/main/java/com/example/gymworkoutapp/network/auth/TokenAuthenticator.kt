package com.example.gymworkoutapp.network.auth

import com.example.gymworkoutapp.auth.SessionManager
import com.example.gymworkoutapp.network.services.AuthService
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(private val authService: AuthService) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val tokenManager = SessionManager.tokenManager()

        val token = tokenManager.getRefreshToken()
        if (token == null) {
            return null
        }

        val refreshResponse = runBlocking {
            authService.refresh(token)
        }

        if (refreshResponse.isSuccessful) {
            val newTokens = refreshResponse.body()
            if (newTokens == null) {
                return null
            }

            tokenManager.saveTokens(newTokens.accessToken, newTokens.refreshToken)

            return response.request().newBuilder()
                .header("Authorization", "Bearer ${newTokens.accessToken}")
                .build()
        }

        return null
    }
}