package com.meadow.app.logging

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton
import com.meadow.core.utils.logging.LoggerContract

@Singleton
class LoggerImpl @Inject constructor() : LoggerContract {

    override fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }
}
