package com.grebnev.cryptoprice.presentation.coinitem.info

import com.grebnev.cryptoprice.domain.entity.Coin

sealed class CoinInfoScreenState {
    data object Initial : CoinInfoScreenState()

    data object Loading : CoinInfoScreenState()

    data class Content(
        val coin: Coin,
    ) : CoinInfoScreenState()

    data class Error(
        val message: String,
    ) : CoinInfoScreenState()
}