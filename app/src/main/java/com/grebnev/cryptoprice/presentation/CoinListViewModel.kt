package com.grebnev.cryptoprice.presentation

import androidx.lifecycle.ViewModel
import com.grebnev.cryptoprice.domain.usecase.GetCoinListUseCase
import com.grebnev.cryptoprice.domain.usecase.LoadDataUseCase
import javax.inject.Inject

class CoinListViewModel @Inject constructor(
    private val loadDataUseCase: LoadDataUseCase,
    private val getCoinListUseCase: GetCoinListUseCase
) : ViewModel() {

    val coinList = getCoinListUseCase()

    init {
        loadDataUseCase()
    }

}