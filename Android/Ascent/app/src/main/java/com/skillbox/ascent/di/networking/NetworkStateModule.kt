package com.skillbox.ascent.di.networking

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkStateModule {

    @Provides
    @Singleton
    fun providesNetworkState(isBanned: Boolean, connectivityManager: ConnectivityManager) : NetworkState {
        return NetworkState(isBanned, connectivityManager)
    }
    @Provides
    @Singleton
    fun providesConnectivityManager(@ApplicationContext context: Context) : ConnectivityManager =
        ContextCompat.getSystemService(
            context,
            ConnectivityManager::class.java
        ) as ConnectivityManager

    @Provides
    @Singleton
    fun providesBannedStatus(@ApplicationContext context: Context) : Boolean {
        val country = context.resources.configuration.locales[0].country
        return country == COUNTRY_CODE_RU || country == COUNTRY_CODE_BY
    }

    companion object {
        private const val COUNTRY_CODE_RU = "RU"
        private const val COUNTRY_CODE_BY = "BY"
    }

}