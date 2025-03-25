package com.grebnev.cryptoprice.presentation.coinitem.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.core.ErrorHandler
import com.grebnev.core.ErrorType
import com.grebnev.core.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Bar
import com.grebnev.cryptoprice.domain.usecase.GetBarsForCoinUseCase
import com.grebnev.cryptoprice.presentation.coinitem.terminal.bars.TerminalBarsState
import com.grebnev.cryptoprice.presentation.coinitem.terminal.bars.TimeFrame
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class TerminalBarsViewModel
    @Inject
    constructor(
        private val getBarsForCoinUseCase: GetBarsForCoinUseCase,
    ) : ViewModel() {
        private val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
                val typeError = ErrorHandler.getErrorTypeByError(throwable)
                _barState.value = TerminalBarsState.Error(typeError.type)
            }

        private val _barState = MutableStateFlow<TerminalBarsState>(TerminalBarsState.Loading)
        val barState: StateFlow<TerminalBarsState> = _barState

        fun loadBarsForCoin(
            timeFrame: TimeFrame,
            fromSymbol: String,
        ) {
            viewModelScope.launch(coroutineExceptionHandler) {
                _barState.value = TerminalBarsState.Loading
                getBarsForCoinUseCase(
                    timeFrame = timeFrame.value,
                    fromSymbol = fromSymbol,
                ).map { resultState ->
                    mapResultStatusToBarState(
                        resultStatus = resultState,
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
            viewModelScope.launch(coroutineExceptionHandler) {
                var isFullScreen = false
                if (barState.value is TerminalBarsState.Content) {
                    isFullScreen = (barState.value as TerminalBarsState.Content).isFullScreen
                }
                _barState.value = TerminalBarsState.Loading
                getBarsForCoinUseCase(
                    timeFrame = timeFrame.value,
                    fromSymbol = fromSymbol,
                ).map { resultState ->
                    mapResultStatusToBarState(
                        resultStatus = resultState,
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

        private fun mapResultStatusToBarState(
            resultStatus: ResultStatus<List<Bar>, ErrorType>,
            timeFrame: TimeFrame,
            isFullScreen: Boolean,
        ): TerminalBarsState =
            when (val currentStatus = resultStatus) {
                is ResultStatus.Error -> TerminalBarsState.Error(currentStatus.error.type)
                ResultStatus.Initial -> TerminalBarsState.Loading
                is ResultStatus.Success -> {
                    val currentBars = currentStatus.data
                    val sortedBar = currentBars.sortedByDescending { it.time }
                    TerminalBarsState.Content(
                        bars = sortedBar,
                        timeFrame = timeFrame,
                        isFullScreen = isFullScreen,
                    )
                }
            }
    }