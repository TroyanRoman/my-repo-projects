package com.skillbox.humblr.di

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {
    @Provides
    @Singleton
    fun provideOnBoardPreference(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.createDataStore(
            name = "com.skillbox.humblr.di.onboard"
        )
    }
}