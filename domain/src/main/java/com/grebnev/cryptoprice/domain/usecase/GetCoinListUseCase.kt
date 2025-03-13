package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.repository.CoinListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoinListUseCase
    @Inject
    constructor(
        private val repository: CoinListRepository,
    ) {
        operator fun invoke(): Flow<List<Coin>> = repository.getCoinList()
    }