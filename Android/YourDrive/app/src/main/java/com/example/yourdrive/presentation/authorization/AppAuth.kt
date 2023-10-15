package com.example.yourdrive.presentation.authorization

import android.net.Uri
import androidx.core.net.toUri
import com.example.yourdrive.presentation.authorization.data.AuthConfig
import com.example.yourdrive.presentation.authorization.data.TokenModel
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientSecretPost
import net.openid.appauth.TokenRequest
import kotlin.coroutines.suspendCoroutine

interface AppAuth {

    fun getAuthRequest(): AuthorizationRequest
    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ): TokenModel

    class Base() : AppAuth {

        private val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.AUTH_URI),
            Uri.parse(AuthConfig.TOKEN_URI)
        )

        override fun getAuthRequest(): AuthorizationRequest {
            val redirectUri = AuthConfig.CALLBACK_URL.toUri()
            return AuthorizationRequest.Builder(
                serviceConfiguration,
                AuthConfig.CLIENT_ID,
                AuthConfig.RESPONSE_TYPE,
                redirectUri
            )
                .setScope(AuthConfig.SCOPE)
                .build()
        }

        override suspend fun performTokenRequest(
            authService: AuthorizationService,
            tokenRequest: TokenRequest
        ): TokenModel {
            return suspendCoroutine { continuation ->
                authService.performTokenRequest(
                    tokenRequest,
                    ClientSecretPost(AuthConfig.CLIENT_SECRET)
                ) { response, ex ->
                    when {
                        response != null -> {
                            val token = TokenModel(
                                accessToken = response.accessToken.orEmpty(),
                                refreshToken = response.refreshToken.orEmpty(),
                                expirationTimeInMillis = response.accessTokenExpirationTime ?: 0
                            )
                            continuation.resumeWith(Result.success(token))
                        }
                        ex != null -> {
                            continuation.resumeWith(Result.failure(ex))
                        }

                        else -> error("unreachable")
                    }
                }
            }
        }
    }
}