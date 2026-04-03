package com.meadow.core.network.utils

import com.meadow.core.utils.coroutines.CoroutineUtils
import com.meadow.core.utils.logging.Logger
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

object NetworkUtils {

    suspend fun isInternetAvailable(): Boolean =
        withContext(CoroutineUtils.IO) {
            return@withContext try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                    Logger.i("NetworkUtils", "Internet connection verified.")
                    true
                }
            } catch (e: Exception) {
                Logger.w("NetworkUtils", "No active internet connection: ${e.message}")
                false
            }
        }
}
