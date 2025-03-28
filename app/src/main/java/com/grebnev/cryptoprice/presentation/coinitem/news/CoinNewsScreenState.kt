package com.grebnev.cryptoprice.presentation.coinitem.news

import com.grebnev.cryptoprice.domain.entity.News

sealed class CoinNewsScreenState {
    data object Initial : CoinNewsScreenState()

    data object Loading : CoinNewsScreenState()

    data class Content(
        val news: List<News>,
    ) : CoinNewsScreenState()

    data class Error(
        val message: String,
    ) : CoinNewsScreenState()
}