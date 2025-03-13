package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.cryptoprice.domain.repository.CoinListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTimeLastUpdate
    @Inject
    constructor(
        private val coinListRepository: CoinListRepository,
    ) {
        operator fun invoke(): Flow<String> = coinListRepository.getTimeLastUpdate()
    }