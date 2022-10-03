package com.skillbox.ascent.di

import com.skillbox.ascent.application.theme_change.AppThemeSelector
import com.skillbox.ascent.di.preferences.DarkThemePrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppThemeSelectorModule {
    @Provides
    @Singleton
    fun providesAppThemeSelector(darkPrefs: DarkThemePrefs): AppThemeSelector =
        AppThemeSelector(darkPrefs)
}