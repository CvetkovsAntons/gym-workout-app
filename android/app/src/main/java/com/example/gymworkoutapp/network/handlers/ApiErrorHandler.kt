package com.example.gymworkoutapp.network.handlers

import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.gymworkoutapp.models.ResponseError
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import retrofit2.Response

object ApiErrorHandler {
    fun extractErrorMessage(response: Response<out Any>): String {
        val errorBody = response.errorBody()?.string()

        return try {
            val gson = Gson()
            val errorResponse = gson.fromJson(errorBody, ResponseError::class.java)
            errorResponse?.error ?: "Unknown error"
        } catch (e: Exception) {
            "Unexpected error"
        }
    }

    fun showError(context: Context, response: Response<*>) {
        val message = extractErrorMessage(response)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}