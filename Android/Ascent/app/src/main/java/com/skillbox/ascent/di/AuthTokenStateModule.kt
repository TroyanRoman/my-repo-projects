package com.skillbox.ascent.di

import com.skillbox.ascent.di.preferences.AuthTokenPreference
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AuthTokenStateModule {
    fun providesAuthTokenState(authPrefs : AuthTokenPreference) : AuthTokenState {
        return AuthTokenState(authPrefs)
    }
}