package com.grebnev.cryptoprice.domain.repository

import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import kotlinx.coroutines.flow.Flow

interface CoinListRepository {
    val getCoinList: Flow<ResultStatus<List<Coin>, ErrorType>>

    suspend fun loadData()

    fun getTimeLastUpdate(): Flow<String>
}