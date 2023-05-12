package ru.example.gnt.common.model

sealed class UiState<out T> {
    object Empty : UiState<Nothing>()
    object Loading: UiState<Nothing>()
    data class Error(val error: Throwable): UiState<Nothing>()
    data class SuccessRemote<T>(val data: T) : UiState<T>()

    data class SuccessCached<T>(val data: T) : UiState<T>()

}
