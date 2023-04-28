package ru.example.gnt.common.utils

import ru.example.gnt.common.model.Resource

sealed class RetrofitResult<out T> {
    object Empty : RetrofitResult<Nothing>()
    data class Error(val message: Throwable): RetrofitResult<Nothing>()
    data class Success<T>(val data: T) : RetrofitResult<T>()
}
