package com.grebnev.cryptoprice.domain.repository

import com.grebnev.core.ErrorType
import com.grebnev.core.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import kotlinx.coroutines.flow.Flow

interface CoinListRepository {
    val getCoinList: Flow<ResultStatus<List<Coin>, ErrorType>>

    suspend fun loadData()

    fun getTimeLastUpdate(): Flow<String>
}