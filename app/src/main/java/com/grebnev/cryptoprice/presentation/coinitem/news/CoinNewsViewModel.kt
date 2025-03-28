package com.grebnev.cryptoprice.presentation.coinitem.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.News
import com.grebnev.cryptoprice.domain.usecase.GetNewsForCoinUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CoinNewsViewModel
    @Inject
    constructor(
        private val getNewsForCoinUseCase: GetNewsForCoinUseCase,
    ) : ViewModel() {
        private val coroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
            }
        private val _screenState = MutableStateFlow<CoinNewsScreenState>(CoinNewsScreenState.Initial)
        val screenState: StateFlow<CoinNewsScreenState> = _screenState

        fun getNewsForCoin(category: String) {
            viewModelScope.launch(coroutineExceptionHandler) {
                _screenState.value = CoinNewsScreenState.Loading
                getNewsForCoinUseCase(category)
                    .map { mapResultStatusToScreenState(it) }
                    .collect { _screenState.value = it }
            }
        }

        private fun mapResultStatusToScreenState(
            resultStatus: ResultStatus<List<News>, ErrorType>,
        ): CoinNewsScreenState =
            when (val currentStatus = resultStatus) {
                is ResultStatus.Error -> CoinNewsScreenState.Error(currentStatus.error.type)
                ResultStatus.Initial -> CoinNewsScreenState.Loading
                is ResultStatus.Success -> CoinNewsScreenState.Content(currentStatus.data)
            }
    }