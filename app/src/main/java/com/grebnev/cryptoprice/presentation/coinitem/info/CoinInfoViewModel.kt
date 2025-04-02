package com.grebnev.cryptoprice.presentation.coinitem.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.grebnev.core.handlers.ErrorHandler
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinInfoUseCase
import com.grebnev.cryptoprice.presentation.base.error.ErrorMessageProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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
                val typeError = ErrorHandler.getErrorTypeByError(throwable)
                _screenState.value =
                    CoinInfoScreenState.Error(errorMessageProvider.getErrorMessage(typeError))
            }
        private val _screenState = MutableStateFlow<CoinInfoScreenState>(CoinInfoScreenState.Initial)
        val screenState: LiveData<CoinInfoScreenState> = _screenState.asLiveData()

        fun getCoinInfo(fromSymbol: String) {
            viewModelScope.launch(coroutineExceptionHandler) {
                getCoinItemUseCase(fromSymbol)
                    .map { mapResultStatusToScreenState(it) }
                    .onStart { emit(CoinInfoScreenState.Loading) }
                    .collect { _screenState.value = it }
            }
        }

        private fun mapResultStatusToScreenState(
            resultStatus: ResultStatus<Coin, ErrorType>,
        ): CoinInfoScreenState =
            when (resultStatus) {
                is ResultStatus.Error ->
                    CoinInfoScreenState.Error(
                        errorMessageProvider.getErrorMessage(resultStatus.error),
                    )
                is ResultStatus.Success -> CoinInfoScreenState.Content(resultStatus.data)
            }
    }