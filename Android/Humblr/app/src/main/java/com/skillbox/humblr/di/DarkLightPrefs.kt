package com.skillbox.humblr.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DarkLightPrefs @Inject constructor(@ApplicationContext context : Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getDarkThemeStatus() : Int =   preferences.getInt(DARK_STATUS, 1)

    fun setDarkThemeStatus (value : Int) {
        preferences.edit().putInt(DARK_STATUS, value).apply()

    }

    companion object {
        private const val DARK_STATUS = "DarkLightStatus"
    }
}