package com.grebnev.cryptoprice.domain.repository

import com.grebnev.core.ErrorType
import com.grebnev.core.ResultState
import com.grebnev.cryptoprice.domain.entity.Bar
import kotlinx.coroutines.flow.Flow

interface BarRepository {
    fun getBarsForCoin(
        fromSymbol: String,
        timeFrame: String,
    ): Flow<ResultState<List<Bar>, ErrorType>>
}