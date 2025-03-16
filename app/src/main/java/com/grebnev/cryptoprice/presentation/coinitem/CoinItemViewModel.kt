package com.grebnev.cryptoprice.presentation.coinitem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.core.ErrorType
import com.grebnev.core.ResultState
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class CoinItemViewModel
    @Inject
    constructor(
        private val getCoinItemUseCase: GetCoinItemUseCase,
    ) : ViewModel() {
        private val _screenState = MutableStateFlow<CoinItemScreenState>(CoinItemScreenState.Loading)
        val screenState: StateFlow<CoinItemScreenState> = _screenState

        fun getCoinItem(fromSymbol: String) {
            viewModelScope.launch {
                getCoinItemUseCase(fromSymbol)
                    .map { mapResultStateToScreenState(it) }
                    .collect { _screenState.value = it }
            }
        }

        private fun mapResultStateToScreenState(
            resultState: ResultState<Coin, ErrorType>,
        ): CoinItemScreenState =
            when (resultState) {
                is ResultState.Error -> CoinItemScreenState.Error(resultState.error.type)
                ResultState.Initial -> CoinItemScreenState.Loading
                is ResultState.Success -> CoinItemScreenState.Success(resultState.data)
            }
    }