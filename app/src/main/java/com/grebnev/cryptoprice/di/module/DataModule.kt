package com.grebnev.cryptoprice.di.module

import com.grebnev.cryptoprice.data.database.CoinDao
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @Binds
    fun bindDatabaseSource(): CoinDao


}