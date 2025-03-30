package com.grebnev.cryptoprice.presentation.coinitem.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinInfoUseCase
import com.grebnev.cryptoprice.presentation.base.ErrorMessageProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CoinInfoViewModel
    @Inject
    constructor(
        private val getCoinItemUseCase: GetCoinInfoUseCase,
        private val errorMessageProvider: ErrorMessageProvider,
    ) : ViewModel() {
        private val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
            }
        private val _screenState = MutableStateFlow<CoinInfoScreenState>(CoinInfoScreenState.Initial)
        val screenState: StateFlow<CoinInfoScreenState> = _screenState

        fun getCoinInfo(fromSymbol: String) {
            viewModelScope.launch(coroutineExceptionHandler) {
                getCoinItemUseCase(fromSymbol)
                    .map { mapResultStatusToScreenState(it) }
                    .collect { _screenState.value = it }
            }
        }

        private fun mapResultStatusToScreenState(
            resultStatus: ResultStatus<Coin, ErrorType>,
        ): CoinInfoScreenState =
            when (val currentStatus = resultStatus) {
                is ResultStatus.Error ->
                    CoinInfoScreenState.Error(
                        errorMessageProvider.getErrorMessage(currentStatus.error),
                    )
                ResultStatus.Initial -> CoinInfoScreenState.Loading
                is ResultStatus.Success -> CoinInfoScreenState.Content(currentStatus.data)
            }
    }