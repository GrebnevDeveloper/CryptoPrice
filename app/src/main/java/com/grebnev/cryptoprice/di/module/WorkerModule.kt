package com.grebnev.cryptoprice.di.module

import com.grebnev.cryptoprice.data.workers.ChildCoinWorkerFactory
import com.grebnev.cryptoprice.data.workers.RefreshDataWorker
import com.grebnev.cryptoprice.di.key.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(RefreshDataWorker::class)
    fun bindRefreshDataWorkerFactory(factory: RefreshDataWorker.Factory): ChildCoinWorkerFactory
}