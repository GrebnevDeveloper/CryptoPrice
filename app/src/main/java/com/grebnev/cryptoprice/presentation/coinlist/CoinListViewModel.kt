package com.grebnev.cryptoprice.presentation.coinlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.core.ErrorType
import com.grebnev.core.ResultState
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinListUseCase
import com.grebnev.cryptoprice.domain.usecase.GetTimeLastUpdate
import com.grebnev.cryptoprice.domain.usecase.LoadDataUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CoinListViewModel
    @Inject
    constructor(
        private val loadDataUseCase: LoadDataUseCase,
        private val getCoinListUseCase: GetCoinListUseCase,
        private val getTimeLastUpdate: GetTimeLastUpdate,
    ) : ViewModel() {
        private val exceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Timber.e(throwable)
            }
        private val coinListFlow = getCoinListUseCase()

        val screenState =
            coinListFlow
                .map { mapResultStateToScreenState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.Lazily,
                    initialValue = CoinListScreenState.Loading,
                )

        private fun mapResultStateToScreenState(
            coinListSate: ResultState<List<Coin>, ErrorType>,
        ): CoinListScreenState =
            when (coinListSate) {
                is ResultState.Error ->
                    CoinListScreenState.Error(coinListSate.error.type)

                ResultState.Initial ->
                    CoinListScreenState.Loading

                is ResultState.Success -> {
                    CoinListScreenState.Success(
                        coinListSate.data,
                        timeLastUpdateState.value,
                    )
                }
            }

        private val timeLastUpdateState = MutableStateFlow<String>("Loading...")

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