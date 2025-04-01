package com.grebnev.cryptoprice.presentation.coinitem.terminal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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
                _barState.value =
                    TerminalBarsScreenState.Error(errorMessageProvider.getErrorMessage(typeError))
            }

        private val _barState = MutableStateFlow<TerminalBarsScreenState>(TerminalBarsScreenState.Initial)
        val barState: LiveData<TerminalBarsScreenState> = _barState.asLiveData()

        private val _timeFrame = MutableStateFlow<TimeFrame>(TimeFrame.DAILY)
        val timeFrame: StateFlow<TimeFrame> = _timeFrame

        fun loadBarsForCoin(fromSymbol: String) {
            viewModelScope.launch(coroutineExceptionHandler) {
                val currentTimeFrame = timeFrame.value
                _barState.value = TerminalBarsScreenState.Loading
                getBarsForCoinUseCase(
                    timeFrame = currentTimeFrame.value,
                    fromSymbol = fromSymbol,
                ).map { mapResultStatusToBarScreenState(it) }
                    .collect { _barState.value = it }
            }
        }

        fun changeTimeFrameStatus(
            timeFrame: TimeFrame,
            fromSymbol: String,
        ) {
            viewModelScope.launch(coroutineExceptionHandler) {
                _timeFrame.value = timeFrame
                _barState.value = TerminalBarsScreenState.Loading
                getBarsForCoinUseCase(
                    timeFrame = timeFrame.value,
                    fromSymbol = fromSymbol,
                ).map { mapResultStatusToBarScreenState(it) }
                    .collect { _barState.value = it }
            }
        }

        private fun mapResultStatusToBarScreenState(
            resultStatus: ResultStatus<List<Bar>, ErrorType>,
        ): TerminalBarsScreenState =
            when (resultStatus) {
                is ResultStatus.Error ->
                    TerminalBarsScreenState.Error(
                        errorMessageProvider.getErrorMessage(resultStatus.error),
                    )

                is ResultStatus.Success -> {
                    val currentBars = resultStatus.data
                    val sortedBar = currentBars.sortedByDescending { it.time }
                    TerminalBarsScreenState.Content(sortedBar)
                }
            }
    }