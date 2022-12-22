package com.grebnev.cryptoprice.di.module

import android.app.Application
import com.grebnev.cryptoprice.data.database.AppDatabase
import com.grebnev.cryptoprice.data.database.CoinDao
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    fun bindDatabaseSource(application: Application): CoinDao {
        return AppDatabase.getInstance(application).coinDao()
    }


}