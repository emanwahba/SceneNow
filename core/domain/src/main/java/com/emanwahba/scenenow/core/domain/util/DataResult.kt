package com.emanwahba.scenenow.core.domain.util

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : DataResult<Nothing>()
}