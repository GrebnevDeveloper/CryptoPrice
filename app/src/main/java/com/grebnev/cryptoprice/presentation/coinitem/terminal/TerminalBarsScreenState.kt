package com.grebnev.cryptoprice.presentation.coinitem.terminal

import com.grebnev.cryptoprice.domain.entity.Bar

sealed class TerminalBarsScreenState {
    data object Initial : TerminalBarsScreenState()

    data object Loading : TerminalBarsScreenState()

    data class Content(
        val bars: List<Bar>,
    ) : TerminalBarsScreenState()

    data class Error(
        val message: String,
    ) : TerminalBarsScreenState()
}