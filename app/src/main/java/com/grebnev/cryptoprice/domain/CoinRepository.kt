package com.grebnev.cryptoprice.domain

import androidx.lifecycle.LiveData
import com.grebnev.cryptoprice.domain.entity.Coin

interface CoinRepository {
    fun getCoinList(): LiveData<List<Coin>>
    fun getCoinItem(fromSymbol: String): LiveData<Coin>
    suspend fun loadData()
}