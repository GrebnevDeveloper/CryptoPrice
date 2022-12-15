package com.grebnev.cryptoprice.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.grebnev.cryptoprice.data.repository.CoinRepositoryImpl
import com.grebnev.cryptoprice.domain.usecase.GetCoinListUseCase
import com.grebnev.cryptoprice.domain.usecase.LoadDataUseCase

class CoinListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CoinRepositoryImpl(application)

    private val loadDataUseCase = LoadDataUseCase(repository)
    private val getCoinListUseCase = GetCoinListUseCase(repository)

    val coinList = getCoinListUseCase()

    init {
        loadDataUseCase()
    }

}