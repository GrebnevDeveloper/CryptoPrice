package com.grebnev.cryptoprice.presentation

import androidx.lifecycle.ViewModel
import com.grebnev.cryptoprice.domain.usecase.GetCoinItemUseCase
import javax.inject.Inject

class CoinItemViewModel @Inject constructor(
    private val getCoinItemUseCase: GetCoinItemUseCase
) : ViewModel() {


    fun getCoinItem(fromSymbol: String) = getCoinItemUseCase(fromSymbol)
}