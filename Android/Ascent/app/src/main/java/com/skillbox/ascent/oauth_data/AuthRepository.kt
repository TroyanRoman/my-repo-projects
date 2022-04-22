package com.skillbox.ascent.oauth_data

import androidx.browser.customtabs.CustomTabsIntent
import com.skillbox.ascent.oauth_data.models.TokenModel
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

interface AuthRepository {

    fun getAuthRequest(): AuthorizationRequest

    fun getAuthService() : AuthorizationService

    fun getCustomTabsIntent(): CustomTabsIntent

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ) : TokenModel

    fun getRefreshRequest(refreshToken  : String): TokenRequest

    fun authServiceDispose()

    fun corruptToken()
}