package com.grebnev.cryptoprice.presentation

import android.app.Application
import androidx.work.Configuration
import com.grebnev.cryptoprice.data.workers.CoinWorkerFactory
import com.grebnev.cryptoprice.di.DaggerApplicationComponent
import javax.inject.Inject

class BaseApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var coinWorkerFactory: CoinWorkerFactory

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
            .setWorkerFactory(coinWorkerFactory)
            .build()
    }
}