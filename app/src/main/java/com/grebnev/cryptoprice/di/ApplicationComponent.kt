package com.grebnev.cryptoprice.di

import android.app.Application
import com.grebnev.cryptoprice.di.module.DataModule
import com.grebnev.cryptoprice.di.module.DomainModule
import com.grebnev.cryptoprice.di.module.ViewModelModule
import com.grebnev.cryptoprice.di.module.WorkerModule
import com.grebnev.cryptoprice.presentation.base.BaseApplication
import com.grebnev.cryptoprice.presentation.coinitem.CoinItemFragment
import com.grebnev.cryptoprice.presentation.coinlist.CoinListFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        DomainModule::class,
        ViewModelModule::class,
        WorkerModule::class
    ]
)
interface ApplicationComponent {

    fun inject(coinListFragment: CoinListFragment)
    fun inject(coinItemFragment: CoinItemFragment)
    fun inject(application: BaseApplication)

    @Component.Factory
    interface ApplicationComponentFactory {
        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}