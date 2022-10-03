package com.skillbox.ascent.oauth_data

import net.openid.appauth.TokenRequest

interface AuthTokenRepository {

    suspend fun performNewAccessTokenRequest()

    suspend fun performTokenRequest(
        tokenRequest: TokenRequest,
    )

    suspend fun corruptAccessToken()

    suspend fun logoutCurrentUser()
}