package com.skillbox.ascent.di.networking

import android.content.Context
import com.skillbox.ascent.di.preferences.AuthTokenPreference
import com.skillbox.ascent.di.preferences.NotificationPrefs
import com.skillbox.ascent.di.preferences.UserDataPreferences
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
        return AuthTokenPreference(appContext)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext appContext: Context)
            = UserDataPreferences(appContext)

    @Provides
    @Singleton
    fun providesNotificationPrefs(@ApplicationContext appContext: Context) = NotificationPrefs(appContext)
}