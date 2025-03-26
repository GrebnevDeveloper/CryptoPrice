package com.grebnev.cryptoprice.presentation.coinitem.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.core.ErrorType
import com.grebnev.core.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinInfoUseCase
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
                is ResultStatus.Error -> CoinInfoScreenState.Error(currentStatus.error.type)
                ResultStatus.Initial -> CoinInfoScreenState.Loading
                is ResultStatus.Success -> CoinInfoScreenState.Content(currentStatus.data)
            }
    }