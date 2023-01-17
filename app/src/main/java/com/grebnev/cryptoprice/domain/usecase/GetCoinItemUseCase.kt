package com.grebnev.cryptoprice.domain.usecase

import com.grebnev.cryptoprice.domain.CoinRepository
import com.grebnev.cryptoprice.domain.entity.Coin
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoinItemUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(fromSymbol: String): Flow<Coin> {
        return repository.getCoinItem(fromSymbol)
    }
}