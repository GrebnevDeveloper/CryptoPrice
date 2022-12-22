package com.grebnev.cryptoprice.presentation

import android.app.Application
import com.grebnev.cryptoprice.di.DaggerApplicationComponent

class BaseApplication : Application() {

    val component by lazy {
        DaggerApplicationComponent.factory()
            .create(this)
    }
}