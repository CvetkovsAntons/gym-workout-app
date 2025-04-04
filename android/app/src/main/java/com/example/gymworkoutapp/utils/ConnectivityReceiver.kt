package com.example.gymworkoutapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

class ConnectivityReceiver(
    private val onNetworkAvailable: () -> Unit,
    private val onNetworkLost: () -> Unit,
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo

            if (networkInfo != null && networkInfo.isConnected) {
                Log.d("ConnectivityReceiver", "Internet is available")
                onNetworkAvailable()
            } else {
                Log.d("ConnectivityReceiver", "No internet")
                onNetworkLost()
            }
        }
    }

}