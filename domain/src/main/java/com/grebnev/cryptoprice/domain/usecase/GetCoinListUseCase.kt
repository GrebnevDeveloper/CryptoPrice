package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoinListUseCase
    @Inject
    constructor(
        private val repository: CoinRepository,
    ) {
        operator fun invoke(): Flow<List<Coin>> = repository.getCoinList()
    }