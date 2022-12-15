package com.grebnev.cryptoprice.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.grebnev.cryptoprice.data.repository.CoinRepositoryImpl
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinItemUseCase
import kotlinx.coroutines.launch

class CoinItemViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CoinRepositoryImpl(application)

    private val getCoinItemUseCase = GetCoinItemUseCase(repository)

    fun getCoinItem(fromSymbol: String) = getCoinItemUseCase(fromSymbol)
}