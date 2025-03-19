package com.grebnev.cryptoprice.presentation.coinitem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.core.ErrorType
import com.grebnev.core.ResultState
import com.grebnev.cryptoprice.domain.entity.Bar
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetBarsForCoinUseCase
import com.grebnev.cryptoprice.domain.usecase.GetCoinItemUseCase
import com.grebnev.cryptoprice.presentation.coinitem.bars.TerminalBarsState
import com.grebnev.cryptoprice.presentation.coinitem.bars.TimeFrame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class CoinItemViewModel
    @Inject
    constructor(
        private val getCoinItemUseCase: GetCoinItemUseCase,
        private val getBarsForCoinUseCase: GetBarsForCoinUseCase,
    ) : ViewModel() {
        private val _screenState = MutableStateFlow<CoinItemScreenState>(CoinItemScreenState.Loading)
        val screenState: StateFlow<CoinItemScreenState> = _screenState

        private val _barState = MutableStateFlow<TerminalBarsState>(TerminalBarsState.Loading)
        val barState: StateFlow<TerminalBarsState> = _barState

        fun getCoinItem(fromSymbol: String) {
            viewModelScope.launch {
                getCoinItemUseCase(fromSymbol)
                    .map { mapResultStateToScreenState(it) }
                    .collect { _screenState.value = it }
            }
        }

        fun loadBarsForCoin(
            timeFrame: TimeFrame,
            fromSymbol: String,
        ) {
            viewModelScope.launch {
                _barState.value = TerminalBarsState.Loading
                getBarsForCoinUseCase(
                    timeFrame = timeFrame.value,
                    fromSymbol = fromSymbol,
                ).map { resultState ->
                    mapResultStateToBarState(
                        resultState = resultState,
                        timeFrame = timeFrame,
                        isFullScreen = false,
                    )
                }.collect { _barState.value = it }
            }
        }

        fun changeTimeFrameStatus(
            timeFrame: TimeFrame,
            fromSymbol: String,
        ) {
            viewModelScope.launch {
                var isFullScreen = false
                if (barState.value is TerminalBarsState.Content) {
                    isFullScreen = (barState.value as TerminalBarsState.Content).isFullScreen
                }
                _barState.value = TerminalBarsState.Loading
                getBarsForCoinUseCase(
                    timeFrame = timeFrame.value,
                    fromSymbol = fromSymbol,
                ).map { resultState ->
                    mapResultStateToBarState(
                        resultState = resultState,
                        timeFrame = timeFrame,
                        isFullScreen = isFullScreen,
                    )
                }.collect { _barState.value = it }
            }
        }

        fun changeFullScreenStatus() {
            val currentBarsState = _barState.value
            if (currentBarsState is TerminalBarsState.Content) {
                _barState.value = currentBarsState.copy(isFullScreen = !currentBarsState.isFullScreen)
            }
        }

        private fun mapResultStateToBarState(
            resultState: ResultState<List<Bar>, ErrorType>,
            timeFrame: TimeFrame,
            isFullScreen: Boolean,
        ): TerminalBarsState =
            when (val currentState = resultState) {
                is ResultState.Error -> TerminalBarsState.Error(currentState.error.type)
                ResultState.Initial -> TerminalBarsState.Loading
                is ResultState.Success -> {
                    val currentBars = currentState.data
                    val sortedBar = currentBars.sortedByDescending { it.time }
                    TerminalBarsState.Content(
                        bars = sortedBar,
                        timeFrame = timeFrame,
                        isFullScreen = isFullScreen,
                    )
                }
            }

        private fun mapResultStateToScreenState(
            resultState: ResultState<Coin, ErrorType>,
        ): CoinItemScreenState =
            when (val currentState = resultState) {
                is ResultState.Error -> CoinItemScreenState.Error(currentState.error.type)
                ResultState.Initial -> CoinItemScreenState.Loading
                is ResultState.Success -> CoinItemScreenState.Success(currentState.data)
            }
    }