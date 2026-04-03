package com.meadow.core.common.functional

suspend fun <T> safeCall(action: suspend () -> T): ResultX<T> = try {
    ResultX.Ok(action())
} catch (e: Exception) {
    ResultX.Err(
        message = e.message ?: "Unknown error",
        cause = e
    )
}