package com.grebnev.cryptoprice.presentation.coinitem.bars

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.grebnev.cryptoprice.domain.entity.Bar
import java.io.Serializable
import kotlin.math.roundToInt

data class TerminalState(
    val bars: List<Bar>,
    val visibleBarsCount: Int = 100,
    val terminalWidth: Float = 1f,
    val terminalHeight: Float = 1f,
    val scrolledBy: Float = 0f,
) : Serializable {
    val barWidth: Float
        get() = terminalWidth / visibleBarsCount

    private val visibleBars: List<Bar>
        get() {
            val startIndex = (scrolledBy / barWidth).roundToInt().coerceAtLeast(0)
            val endIndex = (startIndex + visibleBarsCount).coerceAtMost(bars.size)
            return bars.subList(startIndex, endIndex)
        }

    val max: Double
        get() = visibleBars.maxOf { it.high }

    val min: Double
        get() = visibleBars.minOf { it.low }

    val pxPerPoint: Double
        get() = terminalHeight / (max - min)
}

@Composable
fun rememberTerminalState(bars: List<Bar>): MutableState<TerminalState> =
    rememberSaveable {
        mutableStateOf(TerminalState(bars))
    }