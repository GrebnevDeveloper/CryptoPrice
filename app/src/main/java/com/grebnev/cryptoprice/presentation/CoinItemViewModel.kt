package com.grebnev.cryptoprice.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.grebnev.cryptoprice.data.repository.CoinRepositoryImpl
import com.grebnev.cryptoprice.domain.usecase.GetCoinItemUseCase

class CoinItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CoinRepositoryImpl(application)

    private val getCoinItemUseCase = GetCoinItemUseCase(repository)

    fun getCoinItem(fromSymbol: String) = getCoinItemUseCase(fromSymbol)
}