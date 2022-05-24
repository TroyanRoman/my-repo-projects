package com.skillbox.ascent.oauth_data

import androidx.browser.customtabs.CustomTabsIntent
import com.skillbox.ascent.oauth_data.models.TokenModel
import com.skillbox.ascent.oauth_data.models.TokenRefreshResponse
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenRequest

interface AuthRepository {

    fun getAuthRequest(): AuthorizationRequest

    //fun getAuthService() : AuthorizationService

  //  fun getCustomTabsIntent(): CustomTabsIntent

    fun corruptAccessToken()

    suspend fun logoutCurrentUser()

  //  fun getEndSessionRequest(): EndSessionRequest

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    )

  //  suspend fun getNewAccessToken(refreshToken : String) : TokenRefreshResponse

    suspend fun performNewAccessTokenRequest()

   // fun getRefreshRequest(refreshToken  : String): TokenRequest

//    fun authServiceDispose()


}