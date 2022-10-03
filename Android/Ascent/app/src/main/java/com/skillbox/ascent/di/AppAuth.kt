package com.skillbox.ascent.di

import android.net.Uri
import androidx.core.net.toUri
import com.skillbox.ascent.oauth_data.AuthConfig
import com.skillbox.ascent.oauth_data.models.TokenModel
import net.openid.appauth.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.suspendCoroutine

@Singleton
class AppAuth @Inject constructor() {

    private val serviceConfiguration = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.AUTH_URI),
        Uri.parse(AuthConfig.TOKEN_URI)
    )


    fun getAuthRequest(): AuthorizationRequest {

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

    fun getRefreshRequest(refreshToken: String): TokenRequest {

        return TokenRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID

        ).setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setScopes(AuthConfig.SCOPE)
            .setRefreshToken(refreshToken)
            .build()
    }

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ): TokenModel {
        return suspendCoroutine { continuation ->
            authService.performTokenRequest(
                tokenRequest,
                getClientAuthentication()
            ) { response, ex ->
                when {
                    response != null -> {
                        val token = TokenModel(
                            accessToken = response.accessToken.orEmpty(),
                            refreshToken = response.refreshToken.orEmpty(),
                            expTime = response.accessTokenExpirationTime ?: 0
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


    private fun getClientAuthentication(): ClientAuthentication =
        ClientSecretPost(AuthConfig.CLIENT_SECRET)
}