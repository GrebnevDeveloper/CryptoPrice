package com.grebnev.core

sealed class ResultState<out T, out E> {
    data class Success<out T>(
        val data: T,
    ) : ResultState<T, Nothing>()

    data class Error<out E>(
        val error: E,
    ) : ResultState<Nothing, E>()

    data object Initial : ResultState<Nothing, Nothing>()
}