package com.grebnev.cryptoprice.presentation.coinitem

import com.grebnev.cryptoprice.domain.entity.Coin

sealed class CoinItemScreenState {
    data object Initial : CoinItemScreenState()

    data object Loading : CoinItemScreenState()

    data class Success(
        val coin: Coin,
    ) : CoinItemScreenState()

    data class Error(
        val message: String,
    ) : CoinItemScreenState()
}