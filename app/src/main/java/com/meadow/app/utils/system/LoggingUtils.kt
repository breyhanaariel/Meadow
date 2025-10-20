package com.meadow.app.utils.system

import android.util.Log

/**
 * LoggingUtils.kt
 *
 * Provides structured logs and optional crash reporting hooks.
 */
object LoggingUtils {

    private const val TAG = "MeadowApp"

    fun d(message: String) = Log.d(TAG, message)
    fun e(message: String, throwable: Throwable? = null) = Log.e(TAG, message, throwable)
    fun i(message: String) = Log.i(TAG, message)
    fun w(message: String) = Log.w(TAG, message)
}
