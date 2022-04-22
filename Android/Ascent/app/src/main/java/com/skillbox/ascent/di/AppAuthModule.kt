package com.skillbox.ascent.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppAuthModule {
    @Provides
    @Singleton
    fun providesAppAuth(@ApplicationContext context: Context, authTokenPreference: AuthTokenPreference): AppAuth = AppAuth(context,authTokenPreference)
}