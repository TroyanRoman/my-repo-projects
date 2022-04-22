package com.skillbox.ascent.di

import android.content.Context
import android.net.Uri
import android.util.Log
import com.skillbox.ascent.oauth_data.AuthConfig
import com.skillbox.ascent.oauth_data.models.TokenModel
import dagger.hilt.android.qualifiers.ApplicationContext
import net.openid.appauth.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.suspendCoroutine

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context : Context?,
    private val authTokenPreference: AuthTokenPreference) {

    private val serviceConfiguration = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.AUTH_URI),
        Uri.parse(AuthConfig.TOKEN_URI)
    )
    private val callbackUri = Uri.parse(AuthConfig.CALLBACK_URL)

    fun getAuthRequest(): AuthorizationRequest {

        val redirectUri = Uri.parse(AuthConfig.CALLBACK_URL)


        val requestBuilder = AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setScope(AuthConfig.SCOPE)
            .build()
        Log.d("Auth","get Auth request summonned with builder = $requestBuilder ")
        return requestBuilder
    }

    fun getRefreshRequest(refreshToken: String): TokenRequest {


        return TokenRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID
        ).setRedirectUri(callbackUri)
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
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
                Log.d("Auth","response = ${response.toString()}")

                when {
                    response != null -> {
                        val token = TokenModel(
                            accessToken = response.accessToken.orEmpty(),
                            refreshToken = response.refreshToken.orEmpty(),
                            idToken = response.idToken.orEmpty()
                        )
                        authTokenPreference.setStoredData(token)
                        Log.d("Auth","response = $token")
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

    fun getAuthService(): AuthorizationService {
        val authService = AuthorizationService(context!!)
        Timber.tag("ClientAuth").d("auth service = $authService")
        return authService
    }

    private fun getClientAuthentication(): ClientAuthentication =
        ClientSecretPost(AuthConfig.CLIENT_SECRET)



}