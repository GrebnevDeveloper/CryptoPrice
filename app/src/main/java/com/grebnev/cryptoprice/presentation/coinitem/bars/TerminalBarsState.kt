package com.grebnev.cryptoprice.presentation.coinitem.bars

import com.grebnev.cryptoprice.domain.entity.Bar

sealed class TerminalBarsState {
    data object Initial : TerminalBarsState()

    data object Loading : TerminalBarsState()

    data class Content(
        val bars: List<Bar>,
        val timeFrame: TimeFrame,
    ) : TerminalBarsState()

    data class Error(
        val message: String,
    ) : TerminalBarsState()
}