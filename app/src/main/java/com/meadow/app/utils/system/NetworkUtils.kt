package com.meadow.app.utils.system

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * NetworkUtils.kt
 *
 * Detects if the device has an active internet connection.
 */
object NetworkUtils {

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
