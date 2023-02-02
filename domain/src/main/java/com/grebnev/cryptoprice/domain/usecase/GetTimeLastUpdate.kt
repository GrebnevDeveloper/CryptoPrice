package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.cryptoprice.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTimeLastUpdate @Inject constructor(
    private val coinRepository: CoinRepository
) {
    operator fun invoke(): Flow<String> {
        return coinRepository.getTimeLastUpdate()
    }
}