package com.grebnev.cryptoprice.di.module

import androidx.lifecycle.ViewModel
import com.grebnev.cryptoprice.di.key.ViewModelKey
import com.grebnev.cryptoprice.presentation.coinitem.CoinItemViewModel
import com.grebnev.cryptoprice.presentation.coinlist.CoinListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CoinItemViewModel::class)
    fun bindCoinItemViewModel(impl: CoinItemViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CoinListViewModel::class)
    fun bindCoinListViewModel(impl: CoinListViewModel): ViewModel
}