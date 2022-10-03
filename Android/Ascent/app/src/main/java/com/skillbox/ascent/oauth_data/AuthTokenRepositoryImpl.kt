package com.skillbox.ascent.oauth_data

import android.content.Context
import com.skillbox.ascent.data.ascent.api.AscentUserApi
import com.skillbox.ascent.di.AppAuth
import com.skillbox.ascent.di.AuthTokenState
import com.skillbox.ascent.di.preferences.AuthTokenPreference
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import javax.inject.Inject

class AuthTokenRepositoryImpl @Inject constructor(
    @DispatcherIO val contextDispatcher: CoroutineDispatcher,
    private val authState: AuthTokenState,
    private val ascentUserApi: AscentUserApi,
    private val appAuth: AppAuth,
    private val authPrefs: AuthTokenPreference,
    @ApplicationContext val context: Context,
) : AuthTokenRepository {

    private val authService: AuthorizationService = AuthorizationService(context)
    override suspend fun performNewAccessTokenRequest() {
        val isCurrentTokenInValid = authState.isCurrentTokenInvalid()
        if (isCurrentTokenInValid) {
            withContext(contextDispatcher) {
                val refreshToken = authPrefs.getRequiredToken(AuthConfig.REFRESH_PREF_KEY)
                val newToken = ascentUserApi.getRefreshedAccessToken(refreshToken = refreshToken)

                authPrefs.setStoredData(newToken.toTokenModel())

            }
        }
    }

    override suspend fun performTokenRequest(tokenRequest: TokenRequest) {
        withContext(contextDispatcher) {
            val tokenModel = appAuth.performTokenRequest(authService, tokenRequest)
            authPrefs.setStoredData(tokenModel)

        }
    }

    override suspend fun corruptAccessToken() {
        authPrefs.corruptAccessToken()
    }

    override suspend fun logoutCurrentUser() {
        withContext(contextDispatcher) {
            ascentUserApi.logoutCurrentUser(
                authPrefs.getRequiredToken(AuthConfig.ACCESS_PREF_KEY)
            )
        }
    }
}