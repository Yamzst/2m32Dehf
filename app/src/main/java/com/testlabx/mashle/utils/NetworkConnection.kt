package com.testlabx.mashle.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow

object NetworkConnection {

    val isConnected = MutableStateFlow(false)

    fun initialize(context: Context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().build()
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                isConnected.value = true
            }

            override fun onLost(network: android.net.Network) {
                isConnected.value = false
            }
        }
        cm.registerNetworkCallback(request, callback)
    }
}