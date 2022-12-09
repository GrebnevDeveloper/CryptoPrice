package com.grebnev.cryptoprice.domain.usecase

import androidx.lifecycle.LiveData
import com.grebnev.cryptoprice.domain.CoinRepository
import com.grebnev.cryptoprice.domain.model.Coin

class GetCoinListUseCase(private val repository: CoinRepository) {
    operator fun invoke(): LiveData<List<Coin>> {
        return repository.getCoinList()
    }
}