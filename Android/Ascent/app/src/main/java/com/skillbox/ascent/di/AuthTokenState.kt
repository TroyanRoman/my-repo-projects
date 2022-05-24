package com.skillbox.ascent.di

import com.skillbox.ascent.oauth_data.AuthConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenState @Inject constructor(private val prefs : AuthTokenPreference){

    fun isCurrentTokenInvalid(): Boolean =
        getExistingToken().isEmpty() || getExistingToken() == "corrupted_token" || isExistingTokenExpired()

    fun isCurrentTokenCorrupted() : Boolean = getExistingToken() == "corrupted_token"

    private fun isExistingTokenExpired(): Boolean =
        prefs.getTokenExpirationTime() <= System.currentTimeMillis()

    private fun getExistingToken(): String = prefs.getRequiredToken(AuthConfig.ACCESS_PREF_KEY)
}