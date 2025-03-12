package com.grebnev.cryptoprice.presentation.base

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
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

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(coinWorkerFactory)
            .build()

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
        WorkManager.initialize(this, workManagerConfiguration)
    }
}