package com.grebnev.cryptoprice.di.module

import androidx.lifecycle.ViewModel
import com.grebnev.cryptoprice.di.ViewModelKey
import com.grebnev.cryptoprice.presentation.CoinItemViewModel
import com.grebnev.cryptoprice.presentation.CoinListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CoinItemViewModel::class)
    fun bindCoinItemViewModule(impl: CoinItemViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CoinListViewModel::class)
    fun bindCoinListViewModule(impl: CoinListViewModel): ViewModel
}