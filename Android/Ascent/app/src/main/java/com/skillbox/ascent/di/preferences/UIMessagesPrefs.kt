package com.skillbox.ascent.di.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UIMessagesPrefs @Inject constructor(@ApplicationContext context : Context) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    fun isUIMessageShown(): Boolean = prefs.getBoolean(UI_MESSAGE_HAS_SHOWN, false)

    fun setIsUIMessageShown(value: Boolean) {
        prefs.edit().putBoolean(UI_MESSAGE_HAS_SHOWN, value).apply()
    }

    companion object {
        private const val UI_MESSAGE_HAS_SHOWN = "ui_message_has_shown"
    }
}