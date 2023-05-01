package ru.example.gnt.common.model

sealed class UiState<out T> {
    object Empty : UiState<Nothing>()
    object Loading: UiState<Nothing>()
    data class Error(val message: Throwable): UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()

}
