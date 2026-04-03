package com.meadow.core.network.utils

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import com.meadow.core.common.functional.ResultX

object ApiErrorHandler {
    fun <T> handle(e: Throwable): ResultX<T> = when (e) {
        is HttpException -> {
            val code = e.code()
            val message = when (code) {
                400 -> "Bad request"
                401 -> "Unauthorized"
                403 -> "Forbidden"
                404 -> "Not found"
                in 500..599 -> "Server error"
                else -> "HTTP $code error"
            }
            ResultX.err(message, code, e)
        }
        is SocketTimeoutException -> ResultX.err("Request timed out", cause = e)
        is IOException -> ResultX.err("Network error. Check your connection.", cause = e)
        else -> ResultX.err(e.message ?: "Unknown error", cause = e)
    }
}