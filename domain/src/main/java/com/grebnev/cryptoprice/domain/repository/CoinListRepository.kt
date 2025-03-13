package com.grebnev.cryptoprice.domain.repository

import com.grebnev.cryptoprice.domain.entity.Coin
import kotlinx.coroutines.flow.Flow

interface CoinListRepository {
    fun getCoinList(): Flow<List<Coin>>

    fun loadData()

    fun getTimeLastUpdate(): Flow<String>
}