package com.skillbox.humblr.oauth_data

import android.app.PendingIntent
import androidx.browser.customtabs.CustomTabsIntent
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest

interface AuthRepository {

    fun getAuthRequest(): AuthorizationRequest

    fun getAuthService() : AuthorizationService

    fun getCustomTabsIntent(): CustomTabsIntent

    fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: () -> Unit,
        onError: () -> Unit
    )

    fun getRefreshRequest(): TokenRequest





    fun authServiceDispose()


}