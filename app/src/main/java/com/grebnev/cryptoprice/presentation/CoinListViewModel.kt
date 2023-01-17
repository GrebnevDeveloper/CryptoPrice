package com.grebnev.cryptoprice.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grebnev.cryptoprice.domain.entity.Coin
import com.grebnev.cryptoprice.domain.usecase.GetCoinListUseCase
import com.grebnev.cryptoprice.domain.usecase.LoadDataUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class CoinListViewModel @Inject constructor(
    private val loadDataUseCase: LoadDataUseCase,
    private val getCoinListUseCase: GetCoinListUseCase
) : ViewModel() {

    private val _coinList = MutableLiveData<List<Coin>>()
    val coinList: LiveData<List<Coin>>
        get() = _coinList

    private fun getCoinList() {
        viewModelScope.launch {
            getCoinListUseCase().collect { coinList ->
                _coinList.value = coinList
            }
        }
    }

    init {
        loadDataUseCase()
        getCoinList()
    }

}