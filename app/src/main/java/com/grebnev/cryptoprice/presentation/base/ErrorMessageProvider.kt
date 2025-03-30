package com.grebnev.cryptoprice.presentation.base

import android.content.Context
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.cryptoprice.R
import javax.inject.Inject

class ErrorMessageProvider
    @Inject
    constructor(
        private val context: Context,
    ) {
        fun getErrorMessage(type: ErrorType): String =
            when (type) {
                ErrorType.NETWORK_ERROR ->
                    context.getString(R.string.network_error_message)
                ErrorType.DATABASE_ERROR ->
                    context.getString(R.string.database_error_message)
                ErrorType.UNKNOWN_ERROR ->
                    context.getString(R.string.unknown_error_message)
            }
    }