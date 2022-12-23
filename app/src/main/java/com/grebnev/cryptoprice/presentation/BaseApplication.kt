package com.grebnev.cryptoprice.presentation

import android.app.Application
import androidx.work.Configuration
import com.grebnev.cryptoprice.data.database.AppDatabase
import com.grebnev.cryptoprice.data.mapper.CoinMapper
import com.grebnev.cryptoprice.data.network.ApiFactory
import com.grebnev.cryptoprice.data.workers.RefreshDataWorkerFactory
import com.grebnev.cryptoprice.di.DaggerApplicationComponent
import javax.inject.Inject

class BaseApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var refreshDataWorkerFactory: RefreshDataWorkerFactory

    val component by lazy {
        DaggerApplicationComponent.factory()
            .create(this)
    }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(refreshDataWorkerFactory)
            .build()
    }
}