package com.grebnev.cryptoprice.di

import com.grebnev.cryptoprice.di.module.DataModule
import com.grebnev.cryptoprice.di.module.DomainModule
import dagger.Component

@Component(modules = [DataModule::class, DomainModule::class])
interface ApplicationComponent {
}