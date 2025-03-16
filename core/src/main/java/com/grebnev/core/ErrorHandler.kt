package com.grebnev.core

import java.io.IOException
import java.sql.SQLException

object ErrorHandler {
    fun getErrorTypeByError(throwable: Throwable): ErrorType =
        when (throwable) {
            is IOException -> {
                ErrorType.NETWORK_ERROR
            }
            is SQLException -> {
                ErrorType.DATABASE_ERROR
            }

            else -> {
                ErrorType.UNKNOWN_ERROR
            }
        }

    fun getErrorTypeByValue(type: String?): ErrorType =
        ErrorType.entries.find {
            it.type == type
        } ?: ErrorType.UNKNOWN_ERROR
}