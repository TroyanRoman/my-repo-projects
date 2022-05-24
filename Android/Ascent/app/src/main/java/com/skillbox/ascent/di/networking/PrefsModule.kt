package com.skillbox.ascent.di.networking

import android.content.Context
import android.util.Log
import com.skillbox.ascent.di.AuthTokenPreference
import com.skillbox.ascent.di.NotificationPrefs
import com.skillbox.ascent.di.UserDataPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PrefsModule {
    @Provides
    @Singleton
    fun provideMyPreference(@ApplicationContext appContext: Context): AuthTokenPreference {
        Log.d("Networking","provides auth token prefs")
        return AuthTokenPreference(appContext)
    }

    //временно пока не сделаю бд
    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext appContext: Context)
            = UserDataPreferences(appContext)

    @Provides
    @Singleton
    fun providesNotificationPrefs(@ApplicationContext appContext: Context) = NotificationPrefs(appContext)
}