package com.meadow.app.core.logger

import android.util.Log

/**
 * MeadowLogger.kt
 *
 * A lightweight wrapper around Android's Log class.
 * Helps keep logging consistent throughout the app.
 *
 * Example:
 *     MeadowLogger.d("Database", "Room initialized successfully!")
 */

object MeadowLogger {
    private const val PREFIX = "🌸 Meadow → "

    fun d(tag: String, message: String) {
        Log.d("$PREFIX$tag", message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e("$PREFIX$tag", message, throwable)
    }

    fun i(tag: String, message: String) {
        Log.i("$PREFIX$tag", message)
    }
}
