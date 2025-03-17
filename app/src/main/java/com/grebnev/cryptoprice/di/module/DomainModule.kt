package com.grebnev.cryptoprice.di.module

import com.grebnev.cryptoprice.data.repository.BarRepositoryImpl
import com.grebnev.cryptoprice.data.repository.CoinListRepositoryImpl
import com.grebnev.cryptoprice.data.repository.CoinRepositoryImpl
import com.grebnev.cryptoprice.domain.repository.BarRepository
import com.grebnev.cryptoprice.domain.repository.CoinListRepository
import com.grebnev.cryptoprice.domain.repository.CoinRepository
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {
    @Binds
    fun bindCoinListRepository(impl: CoinListRepositoryImpl): CoinListRepository

    @Binds
    fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository

    @Binds
    fun bindBarRepository(impl: BarRepositoryImpl): BarRepository
}