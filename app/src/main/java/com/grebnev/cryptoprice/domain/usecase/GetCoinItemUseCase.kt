package com.grebnev.cryptoprice.domain.usecase

import androidx.lifecycle.LiveData
import com.grebnev.cryptoprice.domain.CoinRepository
import com.grebnev.cryptoprice.domain.entity.Coin
import javax.inject.Inject

class GetCoinItemUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke(fromSymbol: String): LiveData<Coin> {
        return repository.getCoinItem(fromSymbol)
    }
}