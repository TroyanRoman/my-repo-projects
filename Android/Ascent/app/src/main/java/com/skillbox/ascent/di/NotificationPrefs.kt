package com.skillbox.ascent.di

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationPrefs @Inject constructor(@ApplicationContext context : Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun isMessageShown(): Boolean = prefs.getBoolean(MESSAGE_HAS_SHOWN, false)

    fun setIsMessageShown(value: Boolean) {
        prefs.edit().putBoolean(MESSAGE_HAS_SHOWN, value).apply()
    }

    companion object {
        private const val MESSAGE_HAS_SHOWN = "message_has_shown"
    }
}