package com.grebnev.cryptoprice.presentation.coinlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinListUseCase
import com.grebnev.cryptoprice.domain.usecase.GetTimeLastUpdate
import com.grebnev.cryptoprice.domain.usecase.LoadDataUseCase
import com.grebnev.cryptoprice.presentation.base.error.ErrorMessageProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CoinListViewModel
    @Inject
    constructor(
        private val loadDataUseCase: LoadDataUseCase,
        private val getCoinListUseCase: GetCoinListUseCase,
        private val getTimeLastUpdate: GetTimeLastUpdate,
        private val errorMessageProvider: ErrorMessageProvider,
    ) : ViewModel() {
        private val exceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
            }
        private val coinListFlow = getCoinListUseCase()

        val screenState =
            coinListFlow
                .map { mapResultStateToScreenState(it) }
                .onStart { emit(CoinListScreenState.Loading) }
                .asLiveData()

        private fun mapResultStateToScreenState(
            coinListSate: ResultStatus<List<Coin>, ErrorType>,
        ): CoinListScreenState =
            when (coinListSate) {
                is ResultStatus.Error ->
                    CoinListScreenState.Error(errorMessageProvider.getErrorMessage(coinListSate.error))
                is ResultStatus.Success -> {
                    CoinListScreenState.Content(
                        coinListSate.data,
                        timeLastUpdateState.value,
                    )
                }
            }

        private val timeLastUpdateState = MutableStateFlow<String>("")

        private fun getLastUpdate() {
            viewModelScope.launch(exceptionHandler) {
                getTimeLastUpdate().collect { time ->
                    timeLastUpdateState.value = time
                }
            }
        }

        private fun loadCoinList() {
            viewModelScope.launch(exceptionHandler) {
                loadDataUseCase()
            }
        }

        init {
            loadCoinList()
            getLastUpdate()
        }
    }