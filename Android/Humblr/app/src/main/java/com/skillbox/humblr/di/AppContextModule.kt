package com.skillbox.humblr.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppContextModule {

    @Provides
    @Singleton
    @AppContextQualifier
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }


}