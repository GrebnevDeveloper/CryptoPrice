package com.grebnev.cryptoprice.domain

import androidx.lifecycle.LiveData
import com.grebnev.cryptoprice.domain.model.Coin

interface CoinRepository {
    fun getCoinList(): LiveData<List<Coin>>
    fun getCoinItem(fromSymbol: String): LiveData<Coin>
}