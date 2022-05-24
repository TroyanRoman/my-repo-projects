package com.skillbox.ascent.oauth_data

import android.util.Log
import com.skillbox.ascent.data.ascent.api.AscentUserApi
import com.skillbox.ascent.di.AppAuth
import com.skillbox.ascent.di.AuthTokenPreference
import com.skillbox.ascent.di.AuthTokenState
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import com.skillbox.ascent.oauth_data.models.TokenRefreshResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.openid.appauth.*
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    @DispatcherIO val contextDispatcher: CoroutineDispatcher,
    private val ascentUserApi: AscentUserApi,
    private val appAuth: AppAuth,
    private val authPrefs: AuthTokenPreference,
    private val authState : AuthTokenState
) : AuthRepository {

    override fun getAuthRequest(): AuthorizationRequest {
        Log.d("Auth","get Auth request clicked")
        return appAuth.getAuthRequest()
    }

    override fun corruptAccessToken() {
        authPrefs.corruptAccessToken()
    }

    override    suspend fun logoutCurrentUser() {
        withContext(contextDispatcher) {
            ascentUserApi.logoutCurrentUser(authPrefs.getRequiredToken(AuthConfig.ACCESS_PREF_KEY))
        }
    }


    override suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest
    )  {
        val tokenModel = appAuth.performTokenRequest(authService, tokenRequest)
        authPrefs.setStoredData(tokenModel)
    }

    override suspend fun performNewAccessTokenRequest() {
        val isCurrentTokenInValid = authState.isCurrentTokenInvalid()
        if(isCurrentTokenInValid) {
            withContext(contextDispatcher) {
                val refreshToken = authPrefs.getRequiredToken(AuthConfig.REFRESH_PREF_KEY)
                Log.d("AuthRefresh", "refresh token = $refreshToken")
                val newToken = ascentUserApi.getRefreshedAccessToken(refreshToken = refreshToken).toTokenModel()
                Log.d("AuthRefresh", "new Access token = $newToken")
                authPrefs.setStoredData(newToken)
            }
        }
    }

    private suspend fun getNewAccessToken(refreshToken: String): TokenRefreshResponse {
       return withContext(contextDispatcher) {
            ascentUserApi.getRefreshedAccessToken(refreshToken = refreshToken)
        }
    }



}
