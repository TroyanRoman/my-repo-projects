package com.skillbox.ascent.di

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.skillbox.ascent.oauth_data.AuthConfig
import com.skillbox.ascent.oauth_data.models.TokenModel
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
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

        val requestBuilder = AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setScope(AuthConfig.SCOPE)
            .build()
        Log.d("Auth", "get Auth request summonned with builder = $requestBuilder ")
        return requestBuilder
    }

    fun getRefreshRequest(refreshToken: String): TokenRequest {
        Log.d("Auth", "refresh started ")

        val tokenRequest =
            TokenRequest.Builder(
                serviceConfiguration,
                AuthConfig.CLIENT_ID

            ).setGrantType(GrantTypeValues.REFRESH_TOKEN)

                .setScopes(AuthConfig.SCOPE)
                .setRefreshToken(refreshToken)
                .build()
        Log.d("Auth", "refresh token Request = $tokenRequest ")
        return tokenRequest
    }

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ): TokenModel {
        return suspendCoroutine { continuation ->
            Log.d("Auth", "Auth service performTokenRequest = $authService")
            authService.performTokenRequest(
                tokenRequest,
                getClientAuthentication()
            ) { response, ex ->
                Log.d("Auth", "response = ${response.toString()}")

                when {
                    response != null -> {
                        val token = TokenModel(
                            accessToken = response.accessToken.orEmpty(),
                            refreshToken = response.refreshToken.orEmpty(),
                            expTime = response.accessTokenExpirationTime ?: 0
                        )

                        Log.d("Auth", "response = $token")

                        continuation.resumeWith(Result.success(token))
                    }
                    ex != null -> {
                        Log.d("Auth", "resume with error =  $ex")
                        continuation.resumeWith(Result.failure(ex))
                        Log.d("Auth", "auth exception =  $ex")

                    }

                    else -> error("unreachable")
                }
            }
        }

    }





private fun getClientAuthentication(): ClientAuthentication =
    ClientSecretPost(AuthConfig.CLIENT_SECRET)


}