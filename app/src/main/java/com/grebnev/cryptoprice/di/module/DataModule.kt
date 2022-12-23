package com.grebnev.cryptoprice.di.module

import android.app.Application
import com.grebnev.cryptoprice.data.database.AppDatabase
import com.grebnev.cryptoprice.data.database.CoinDao
import com.grebnev.cryptoprice.data.network.ApiFactory
import com.grebnev.cryptoprice.data.network.ApiService
import com.grebnev.cryptoprice.di.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @ApplicationScope
    @Provides
    fun bindDatabase(application: Application): CoinDao {
        return AppDatabase.getInstance(application).coinDao()
    }

    @ApplicationScope
    @Provides
    fun bindApiService(): ApiService {
        return ApiFactory.apiService
    }
}