package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.cryptoprice.domain.repository.CoinRepository
import javax.inject.Inject

class LoadDataUseCase
    @Inject
    constructor(
        private val repository: CoinRepository,
    ) {
        operator fun invoke() = repository.loadData()
    }