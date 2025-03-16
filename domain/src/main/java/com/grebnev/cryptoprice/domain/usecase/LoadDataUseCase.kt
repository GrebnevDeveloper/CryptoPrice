package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.cryptoprice.domain.repository.CoinListRepository
import javax.inject.Inject

class LoadDataUseCase
    @Inject
    constructor(
        private val repository: CoinListRepository,
    ) {
        suspend operator fun invoke() = repository.loadData()
    }