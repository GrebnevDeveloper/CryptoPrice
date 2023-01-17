package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.cryptoprice.domain.CoinRepository
import com.grebnev.cryptoprice.domain.entity.Coin
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoinListUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(): Flow<List<Coin>> {
        return repository.getCoinList()
    }
}