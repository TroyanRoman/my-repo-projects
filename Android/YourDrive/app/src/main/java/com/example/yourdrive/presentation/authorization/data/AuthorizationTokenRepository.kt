package com.example.yourdrive.presentation.authorization.data

import net.openid.appauth.TokenRequest

interface AuthorizationTokenRepository : LogoutUser {
    suspend fun refreshToken()
    suspend fun requestToken(
        tokenRequest: TokenRequest,
    )

    class Base() : AuthorizationTokenRepository {
        override suspend fun refreshToken() {
            TODO("Not yet implemented")
        }

        override suspend fun requestToken(tokenRequest: TokenRequest) {
            TODO("Not yet implemented")
        }

        override suspend fun corruptAccessToken() {
            TODO("Not yet implemented")
        }

        override suspend fun logoutCurrentUser() {
            TODO("Not yet implemented")
        }
    }

}

interface LogoutUser {
    suspend fun corruptAccessToken()
    suspend fun logoutCurrentUser()
}
