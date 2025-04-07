package com.grebnev.cryptoprice.di.module

import androidx.lifecycle.ViewModel
import com.grebnev.cryptoprice.di.key.ViewModelKey
import com.grebnev.cryptoprice.presentation.coinitem.info.CoinInfoViewModel
import com.grebnev.cryptoprice.presentation.coinitem.news.CoinNewsViewModel
import com.grebnev.cryptoprice.presentation.coinitem.terminal.TerminalBarsViewModel
import com.grebnev.cryptoprice.presentation.coinlist.CoinListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CoinInfoViewModel::class)
    fun bindCoinInfoViewModel(impl: CoinInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TerminalBarsViewModel::class)
    fun bindTerminalBarsViewModel(impl: TerminalBarsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CoinNewsViewModel::class)
    fun bindCoinNewsViewModel(impl: CoinNewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CoinListViewModel::class)
    fun bindCoinListViewModel(impl: CoinListViewModel): ViewModel
}