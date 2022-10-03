package com.skillbox.ascent.di

import android.util.Log
import com.skillbox.ascent.di.preferences.AuthTokenPreference
import com.skillbox.ascent.oauth_data.AuthConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenState @Inject constructor(
    private val prefs: AuthTokenPreference
) {

    fun isCurrentTokenInvalid(): Boolean {
        val existingToken = prefs.getRequiredToken(AuthConfig.ACCESS_PREF_KEY)
        return existingToken.isEmpty() || existingToken == "corrupted_token" || isExistingTokenExpired()
    }

    fun isCurrentTokenCorrupted(): Boolean =
        prefs.getRequiredToken(AuthConfig.ACCESS_PREF_KEY) == "corrupted_token"

    private fun isExistingTokenExpired(): Boolean {
        val tokenExpirationTime = prefs.getTokenExpirationTime()
        Log.d("AuthRefresh", "expiration time = ${tokenExpirationTime!!}")
        return tokenExpirationTime <= System.currentTimeMillis()
    }
}


