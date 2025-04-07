package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.cryptoprice.domain.repository.CoinListRepository
import javax.inject.Inject

class GetCoinListUseCase
    @Inject
    constructor(
        private val repository: CoinListRepository,
    ) {
        operator fun invoke() = repository.getCoinList
    }