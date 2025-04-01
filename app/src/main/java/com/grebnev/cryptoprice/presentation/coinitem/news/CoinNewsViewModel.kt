package com.grebnev.cryptoprice.presentation.coinitem.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.grebnev.core.handlers.ErrorHandler
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.News
import com.grebnev.cryptoprice.domain.usecase.GetNewsForCoinUseCase
import com.grebnev.cryptoprice.presentation.base.error.ErrorMessageProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CoinNewsViewModel
    @Inject
    constructor(
        private val getNewsForCoinUseCase: GetNewsForCoinUseCase,
        private val errorMessageProvider: ErrorMessageProvider,
    ) : ViewModel() {
        private val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
                val typeError = ErrorHandler.getErrorTypeByError(throwable)
                _screenState.value =
                    CoinNewsScreenState.Error(errorMessageProvider.getErrorMessage(typeError))
            }
        private val _screenState = MutableStateFlow<CoinNewsScreenState>(CoinNewsScreenState.Initial)
        val screenState: LiveData<CoinNewsScreenState> = _screenState.asLiveData()

        fun getNewsForCoin(category: String) {
            viewModelScope.launch(coroutineExceptionHandler) {
                _screenState.value = CoinNewsScreenState.Loading
                getNewsForCoinUseCase(category)
                    .map { mapResultStatusToScreenState(it) }
                    .onStart { CoinNewsScreenState.Loading }
                    .collect { _screenState.value = it }
            }
        }

        private fun mapResultStatusToScreenState(
            resultStatus: ResultStatus<List<News>, ErrorType>,
        ): CoinNewsScreenState =
            when (resultStatus) {
                is ResultStatus.Error ->
                    CoinNewsScreenState.Error(
                        errorMessageProvider.getErrorMessage(resultStatus.error),
                    )
                is ResultStatus.Success -> CoinNewsScreenState.Content(resultStatus.data)
            }
    }