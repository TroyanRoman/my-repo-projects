package com.example.yourdrive.sl

import android.content.Context
import android.content.SharedPreferences
import com.example.yourdrive.data.cache.ProvideSharedPreferences
import com.example.yourdrive.data.cloud.ProvideHttpClientBuilder
import com.example.yourdrive.data.cloud.ProvideOkHttpClientBuilder
import com.github.johnnysc.coremvvm.core.Dispatchers
import com.github.johnnysc.coremvvm.core.ManageResources
import com.github.johnnysc.coremvvm.data.ProvideInterceptor

import com.github.johnnysc.coremvvm.presentation.NavigationCommunication

interface CoreModule : ProvideSharedPreferences, ProvideHttpClientBuilder,
    ManageResources, ProvideNavigation {

    fun provideDispatchers(): Dispatchers

    class Base(context: Context) : CoreModule {
        private val sharedPrefs = ProvideSharedPreferences.Base(context)

        private val provideHttpClientBuilder by lazy {
            ProvideOkHttpClientBuilder.AddInterceptor(
                ProvideInterceptor.Debug(),
                ProvideOkHttpClientBuilder.Base()
            )
        }

        private val dispatchers: Dispatchers = Dispatchers.Base()
        private val manageResources = ManageResources.Base(context)
        private val navigation = NavigationCommunication.Base()

        override fun provideDispatchers(): Dispatchers = dispatchers

        override fun sharedPreferences(): SharedPreferences = sharedPrefs.sharedPreferences()
        override fun provideHttpClientBuilder(): ProvideOkHttpClientBuilder =
            provideHttpClientBuilder


        override fun string(id: Int): String = manageResources.string(id)

        override fun provideNavigation(): NavigationCommunication.Mutable = navigation
    }
}
