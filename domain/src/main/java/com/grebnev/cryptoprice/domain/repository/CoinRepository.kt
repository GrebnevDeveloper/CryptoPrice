package com.grebnev.cryptoprice.domain.repository

import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun getCoinInfo(fromSymbol: String): Flow<ResultStatus<Coin, ErrorType>>
}