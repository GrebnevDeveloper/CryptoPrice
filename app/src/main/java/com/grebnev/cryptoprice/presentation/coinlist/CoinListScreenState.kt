package com.grebnev.cryptoprice.presentation.coinlist

import com.grebnev.cryptoprice.domain.entity.Coin

sealed class CoinListScreenState {
    data object Initial : CoinListScreenState()

    data object Loading : CoinListScreenState()

    data class Success(
        val coins: List<Coin>,
        val timeLastUpdate: String,
    ) : CoinListScreenState()

    data class Error(
        val message: String,
    ) : CoinListScreenState()
}