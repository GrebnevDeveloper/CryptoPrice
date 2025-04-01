package com.grebnev.cryptoprice.presentation.coinitem.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.core.handlers.ErrorHandler
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Bar
import com.grebnev.cryptoprice.domain.usecase.GetBarsForCoinUseCase
import com.grebnev.cryptoprice.presentation.base.error.ErrorMessageProvider
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
        private val errorMessageProvider: ErrorMessageProvider,
    ) : ViewModel() {
        private val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
                val typeError = ErrorHandler.getErrorTypeByError(throwable)
                _barState.value = TerminalBarsScreenState.Error(typeError.type)
            }

        private val _barState = MutableStateFlow<TerminalBarsScreenState>(TerminalBarsScreenState.Initial)
        val barState: StateFlow<TerminalBarsScreenState> = _barState

        private val _timeFrame = MutableStateFlow<TimeFrame>(TimeFrame.DAILY)
        val timeFrame: StateFlow<TimeFrame> = _timeFrame

        fun loadBarsForCoin(fromSymbol: String) {
            viewModelScope.launch(coroutineExceptionHandler) {
                _barState.value = TerminalBarsScreenState.Loading
                val currentTimeFrame = timeFrame.value
                getBarsForCoinUseCase(
                    timeFrame = currentTimeFrame.value,
                    fromSymbol = fromSymbol,
                ).map { resultState ->
                    mapResultStatusToBarScreenState(
                        resultStatus = resultState,
                    )
                }.collect { _barState.value = it }
            }
        }

        fun changeTimeFrameStatus(
            timeFrame: TimeFrame,
            fromSymbol: String,
        ) {
            viewModelScope.launch(coroutineExceptionHandler) {
                _barState.value = TerminalBarsScreenState.Loading
                _timeFrame.value = timeFrame
                getBarsForCoinUseCase(
                    timeFrame = timeFrame.value,
                    fromSymbol = fromSymbol,
                ).map { resultState ->
                    mapResultStatusToBarScreenState(
                        resultStatus = resultState,
                    )
                }.collect { _barState.value = it }
            }
        }

        private fun mapResultStatusToBarScreenState(
            resultStatus: ResultStatus<List<Bar>, ErrorType>,
        ): TerminalBarsScreenState =
            when (val currentStatus = resultStatus) {
                is ResultStatus.Error ->
                    TerminalBarsScreenState.Error(
                        errorMessageProvider.getErrorMessage(currentStatus.error),
                    )
                is ResultStatus.Success -> {
                    val currentBars = currentStatus.data
                    val sortedBar = currentBars.sortedByDescending { it.time }
                    TerminalBarsScreenState.Content(sortedBar)
                }
            }
    }