package com.grebnev.cryptoprice.di

import android.app.Application
import com.grebnev.cryptoprice.di.module.AppModule
import com.grebnev.cryptoprice.di.module.DataModule
import com.grebnev.cryptoprice.di.module.DomainModule
import com.grebnev.cryptoprice.di.module.ViewModelModule
import com.grebnev.cryptoprice.di.module.WorkerModule
import com.grebnev.cryptoprice.presentation.base.BaseApplication
import com.grebnev.cryptoprice.presentation.coinitem.info.CoinInfoFragment
import com.grebnev.cryptoprice.presentation.coinitem.news.CoinNewsFragment
import com.grebnev.cryptoprice.presentation.coinitem.terminal.TerminalBarsFragment
import com.grebnev.cryptoprice.presentation.coinlist.CoinListFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        AppModule::class,
        DataModule::class,
        DomainModule::class,
        ViewModelModule::class,
        WorkerModule::class,
    ],
)
interface ApplicationComponent {
    fun inject(coinListFragment: CoinListFragment)

    fun inject(coinInfoFragment: CoinInfoFragment)

    fun inject(terminalBarsFragment: TerminalBarsFragment)

    fun inject(coinNewsFragment: CoinNewsFragment)

    fun inject(application: BaseApplication)

    @Component.Factory
    interface ApplicationComponentFactory {
        fun create(
            @BindsInstance application: Application,
        ): ApplicationComponent
    }
}