package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.core.ErrorType
import com.grebnev.core.ResultState
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoinItemUseCase
    @Inject
    constructor(
        private val repository: CoinRepository,
    ) {
        operator fun invoke(fromSymbol: String): Flow<ResultState<Coin, ErrorType>> =
            repository.getCoinItem(fromSymbol)
    }