package com.example.gymworkoutapp.network.client

import com.example.gymworkoutapp.auth.SessionManager
import com.example.gymworkoutapp.network.auth.TokenAuthenticator
import com.example.gymworkoutapp.network.interceptor.AuthInterceptor
import com.example.gymworkoutapp.network.services.AuthService
import com.example.gymworkoutapp.network.services.UserService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
//    private const val BASE_URL = "http://10.0.2.2"
    private const val BASE_URL = "http://192.168.0.185"
    private const val AUTH_URL = "$BASE_URL:8080"

    private fun getBase(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(AUTH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.Companion())
    }

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .authenticator(TokenAuthenticator(authService))
            .build()
    }

    val authService: AuthService by lazy {
        getBase().build().create(AuthService::class.java)
    }

    val userService: UserService by lazy {
        getBase()
            .client(provideOkHttpClient())
            .build()
            .create(UserService::class.java)
    }
}