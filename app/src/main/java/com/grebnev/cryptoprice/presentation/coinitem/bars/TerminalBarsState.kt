package com.grebnev.cryptoprice.presentation.coinitem.bars

import com.grebnev.cryptoprice.domain.entity.Bar

sealed class TerminalBarsState {
    data object Initial : TerminalBarsState()

    data object Loading : TerminalBarsState()

    data class Success(
        val bars: List<Bar>,
    ) : TerminalBarsState()

    data class Error(
        val message: String,
    ) : TerminalBarsState()
}