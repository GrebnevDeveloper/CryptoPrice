package com.grebnev.cryptoprice.domain.repository

import com.grebnev.core.ErrorType
import com.grebnev.core.ResultState
import com.grebnev.cryptoprice.domain.entity.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun getCoinItem(fromSymbol: String): Flow<ResultState<Coin, ErrorType>>
}