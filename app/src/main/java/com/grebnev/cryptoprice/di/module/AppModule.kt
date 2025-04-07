package com.grebnev.cryptoprice.di.module

import android.app.Application
import android.content.Context
import com.grebnev.cryptoprice.di.ApplicationScope
import com.grebnev.cryptoprice.presentation.base.error.ErrorMessageProvider
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    @ApplicationScope
    @Provides
    fun provideApplicationContext(application: Application): Context = application.applicationContext

    @ApplicationScope
    @Provides
    fun provideErrorMessage(context: Context): ErrorMessageProvider = ErrorMessageProvider(context)
}