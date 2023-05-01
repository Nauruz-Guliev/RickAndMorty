package ru.example.gnt.common.utils


sealed class DataResource<out T> {
    data class Error<T>(val message: Throwable, val data : T) : DataResource<Nothing>()
    data class Success<T>(val data: T) : DataResource<T>()
}
