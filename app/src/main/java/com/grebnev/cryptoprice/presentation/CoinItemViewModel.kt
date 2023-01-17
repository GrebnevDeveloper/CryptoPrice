package com.grebnev.cryptoprice.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinItemUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class CoinItemViewModel @Inject constructor(
    private val getCoinItemUseCase: GetCoinItemUseCase
) : ViewModel() {

    private val _coinItem = MutableLiveData<Coin>()
    val coinItem: LiveData<Coin>
        get() = _coinItem

    fun getCoinItem(fromSymbol: String) {
        viewModelScope.launch {
            getCoinItemUseCase(fromSymbol).collect { item ->
                _coinItem.value = item
            }
        }
    }
}