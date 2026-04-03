package com.meadow.core.utils.logging

import android.util.Log
import com.meadow.core.utils.logging.LoggerContract

object Logger : LoggerContract {

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
