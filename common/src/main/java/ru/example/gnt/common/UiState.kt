package ru.example.gnt.common

sealed class UiState<out T> {
    object Empty : UiState<Nothing>()
    object Loading: UiState<Nothing>()
    data class Error(val message: Resource.String): UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
}
