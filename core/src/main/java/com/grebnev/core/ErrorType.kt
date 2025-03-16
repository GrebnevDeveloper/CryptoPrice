package com.grebnev.core

enum class ErrorType(
    val type: String,
) {
    NETWORK_ERROR("network_error"),
    DATABASE_ERROR("database_error"),
    UNKNOWN_ERROR("unknown_error"),
}