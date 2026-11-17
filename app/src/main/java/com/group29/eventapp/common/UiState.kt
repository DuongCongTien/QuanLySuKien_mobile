package com.group29.eventapp.common

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Empty(val message: String = "Không có dữ liệu hiển thị") : UiState<Nothing>()
    data class Error(val error: AppError, val message: String = error.defaultMessage) : UiState<Nothing>()

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isEmpty: Boolean get() = this is Empty

    fun getOrNull(): T? = (this as? Success)?.data
}
