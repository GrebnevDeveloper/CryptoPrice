package com.grebnev.cryptoprice.domain

import com.grebnev.cryptoprice.domain.entity.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun getCoinList(): Flow<List<Coin>>
    fun getCoinItem(fromSymbol: String): Flow<Coin>
    fun loadData()
}