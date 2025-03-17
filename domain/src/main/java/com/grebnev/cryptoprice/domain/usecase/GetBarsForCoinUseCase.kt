package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.core.ErrorType
import com.grebnev.core.ResultState
import com.grebnev.cryptoprice.domain.entity.Bar
import com.grebnev.cryptoprice.domain.repository.BarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBarsForCoinUseCase
    @Inject
    constructor(
        private val repository: BarRepository,
    ) {
        operator fun invoke(
            timeFrame: String,
            fromSymbol: String,
        ): Flow<ResultState<List<Bar>, ErrorType>> =
            repository.getBarsForCoin(
                timeFrame = timeFrame,
                fromSymbol = fromSymbol,
            )
    }