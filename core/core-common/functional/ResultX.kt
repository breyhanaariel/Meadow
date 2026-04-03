package com.meadow.core.common.functional

sealed class ResultX<out T> {
    data class Ok<T>(val value: T) : ResultX<T>()
    data class Err(
        val message: String,
        val code: Int? = null,
        val cause: Throwable? = null
    ) : ResultX<Nothing>()

    inline fun <R> map(transform: (T) -> R): ResultX<R> = when (this) {
        is Ok -> Ok(transform(value))
        is Err -> this
    }

    inline fun <R> flatMap(transform: (T) -> ResultX<R>): ResultX<R> = when (this) {
        is Ok -> transform(value)
        is Err -> this
    }

    inline fun onSuccess(block: (T) -> Unit): ResultX<T> = apply { if (this is Ok) block(value) }
    inline fun onError(block: (Err) -> Unit): ResultX<T> = apply { if (this is Err) block(this) }

    companion object {
        fun <T> ok(value: T): ResultX<T> = Ok(value)
        fun <T> success(value: T): ResultX<T> = Ok(value)
        fun err(message: String, code: Int? = null, cause: Throwable? = null): ResultX<Nothing> =
            Err(message, code, cause)
    }

}
