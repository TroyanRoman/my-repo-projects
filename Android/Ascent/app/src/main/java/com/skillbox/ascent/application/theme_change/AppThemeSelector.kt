package com.skillbox.ascent.application.theme_change

import androidx.appcompat.app.AppCompatDelegate

import com.skillbox.ascent.di.preferences.DarkThemePrefs
import com.skillbox.ascent.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppThemeSelector @Inject constructor(
   private val darkPrefs: DarkThemePrefs
) {

    fun changeAppTheme() {
        when (darkPrefs.getDarkThemeStatus()) {
            Constants.LIGHT_THEME_CHOICE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                darkPrefs.setDarkThemeStatus(Constants.DARK_THEME_CHOICE)
            }
            Constants.DARK_THEME_CHOICE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                darkPrefs.setDarkThemeStatus(Constants.LIGHT_THEME_CHOICE)
            }
        }
    }

    fun setAppTheme() {
        val dayNightMode = darkPrefs.getDarkThemeStatus()
        AppCompatDelegate.setDefaultNightMode(dayNightMode)
    }
}