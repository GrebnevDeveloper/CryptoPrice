package com.grebnev.cryptoprice.di

import android.app.Application
import com.grebnev.cryptoprice.di.module.DataModule
import com.grebnev.cryptoprice.di.module.DomainModule
import com.grebnev.cryptoprice.di.module.ViewModelModule
import com.grebnev.cryptoprice.presentation.CoinItemFragment
import com.grebnev.cryptoprice.presentation.CoinListFragment
import dagger.BindsInstance
import dagger.Component

@Component(modules = [DataModule::class, DomainModule::class, ViewModelModule::class])
interface ApplicationComponent {

    fun inject(coinListFragment: CoinListFragment)
    fun inject(coinItemFragment: CoinItemFragment)

    @Component.Factory
    interface ApplicationComponentFactory {
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}