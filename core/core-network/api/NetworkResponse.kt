package com.meadow.core.network.api

sealed class NetworkResponse<out T> {
    data class Success<T>(val data: T) : NetworkResponse<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResponse<Nothing>()
    object Loading : NetworkResponse<Nothing>()
}