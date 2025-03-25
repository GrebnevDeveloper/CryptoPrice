package com.grebnev.core

sealed class ResultStatus<out T, out E> {
    data class Success<out T>(
        val data: T,
    ) : ResultStatus<T, Nothing>()

    data class Error<out E>(
        val error: E,
    ) : ResultStatus<Nothing, E>()

    data object Initial : ResultStatus<Nothing, Nothing>()
}