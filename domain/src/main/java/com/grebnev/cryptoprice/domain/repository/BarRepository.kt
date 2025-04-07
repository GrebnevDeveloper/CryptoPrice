package com.grebnev.cryptoprice.domain.repository

import com.grebnev.core.wrappers.ErrorType
import com.grebnev.core.wrappers.ResultStatus
import com.grebnev.cryptoprice.domain.entity.Bar
import kotlinx.coroutines.flow.Flow

interface BarRepository {
    fun getBarsForCoin(
        fromSymbol: String,
        timeFrame: String,
    ): Flow<ResultStatus<List<Bar>, ErrorType>>
}