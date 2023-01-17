package com.grebnev.cryptoprice.di.module

import com.grebnev.cryptoprice.data.repository.CoinRepositoryImpl
import com.grebnev.cryptoprice.domain.repository.CoinRepository
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    fun bindRepository(impl: CoinRepositoryImpl): CoinRepository
}