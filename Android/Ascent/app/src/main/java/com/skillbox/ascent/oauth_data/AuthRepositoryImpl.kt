package com.skillbox.ascent.oauth_data

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.skillbox.ascent.di.AppAuth
import com.skillbox.ascent.di.AuthTokenPreference
import com.skillbox.ascent.oauth_data.models.TokenModel
import net.openid.appauth.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine


class AuthRepositoryImpl @Inject constructor(
    private val context: Context,
    private val appAuth: AppAuth,
    private val authPrefs: AuthTokenPreference
) : AuthRepository {


    /*
  private val authState = AuthState(
      AuthorizationServiceConfiguration(
          Uri.parse(AuthConfig.AUTH_URI),
          Uri.parse(AuthConfig.TOKEN_URI)
      )
  )

  private val serviceConfiguration = AuthorizationServiceConfiguration(
      Uri.parse(AuthConfig.AUTH_URI),
      Uri.parse(AuthConfig.TOKEN_URI)
  )

  override fun getAuthRequest(): AuthorizationRequest {

      val redirectUri = Uri.parse(AuthConfig.CALLBACK_URL)

      return AuthorizationRequest.Builder(
          serviceConfiguration,
          AuthConfig.CLIENT_ID,
          AuthConfig.RESPONSE_TYPE,
          redirectUri
      )
          .setScope(AuthConfig.SCOPE)
          .build()
  }

  override fun getAuthService(): AuthorizationService {
      val authService = AuthorizationService(context)
      Timber.tag("ClientAuth").d("auth service = $authService")
      return authService
  }


  @RequiresApi(Build.VERSION_CODES.O)
  override suspend fun performTokenRequest(
      authService: AuthorizationService,
      tokenRequest: TokenRequest,
  ): TokenModel {
      return suspendCoroutine { continuation ->

          authService.performTokenRequest(
              tokenRequest,
              getClientAuthentication()
          ) { response, ex ->
              Timber.tag("TokenStatus").d("response = ${response.toString()}")

              when {
                  response != null -> {
                      val token = TokenModel(
                          accessToken = response.accessToken.orEmpty(),
                          refreshToken = response.refreshToken.orEmpty(),
                          idToken = response.idToken.orEmpty()
                      )
                      authTokenPreference.setStoredData(token)
                      //  onComplete()
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


  override fun getRefreshRequest(refreshToken: String): TokenRequest {


      return TokenRequest.Builder(
          serviceConfiguration,
          AuthConfig.CLIENT_ID
      )
          .setRedirectUri(redirectUri)
          .setGrantType(GrantTypeValues.REFRESH_TOKEN)
          .setScopes(AuthConfig.SCOPE)
          .setRefreshToken(refreshToken)
          .build()

  }


  override fun authServiceDispose() {
      AuthorizationService(context).dispose()
  }

  override fun getCustomTabsIntent(): CustomTabsIntent {
      return CustomTabsIntent.Builder()
          .setToolbarColor(ContextCompat.getColor(context, R.color.browser_actions_title_color))
          .build()
  }

  private fun getClientAuthentication(): ClientAuthentication =
      ClientSecretPost(AuthConfig.CLIENT_SECRET)


-----------------------------------------------------


     */
    override fun getAuthRequest(): AuthorizationRequest {
        Log.d("Auth","get Auth request clicked")
        return appAuth.getAuthRequest()
    }

    override fun getAuthService(): AuthorizationService {
        val authService = AuthorizationService(context)
        Timber.tag("ClientAuth").d("auth service = $authService")

        return authService
    }

    override fun getCustomTabsIntent(): CustomTabsIntent {
        return CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(context, R.color.browser_actions_title_color))
            .build()
    }

    override suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest
    ): TokenModel {
        return appAuth.performTokenRequest(authService, tokenRequest)
    }

    override fun getRefreshRequest(refreshToken: String): TokenRequest {
        return appAuth.getRefreshRequest(refreshToken)
    }

    override fun authServiceDispose() {
        AuthorizationService(context).dispose()
    }


    override fun corruptToken() {
        val logoutData = TokenModel(
            "corrupted_token",
            "corrupted_token",
            "corrupted_token"
        )
        authPrefs.setStoredData(logoutData)
    }


}
