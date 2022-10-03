package com.skillbox.ascent.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppAuthModule {
    @Provides
    @Singleton
    fun providesAppAuth(): AppAuth = AppAuth()
}