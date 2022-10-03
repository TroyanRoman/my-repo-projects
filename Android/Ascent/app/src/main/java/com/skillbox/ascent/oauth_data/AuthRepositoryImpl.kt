package com.skillbox.ascent.oauth_data

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import com.skillbox.ascent.data.ascent.api.AscentUserApi
import com.skillbox.ascent.di.AppAuth
import com.skillbox.ascent.di.preferences.AuthTokenPreference
import com.skillbox.ascent.di.AuthTokenState
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.openid.appauth.*
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    @DispatcherIO val contextDispatcher: CoroutineDispatcher,
    @ApplicationContext val context: Context,
    private val appAuth: AppAuth,
) : AuthRepository {

    private val authService: AuthorizationService = AuthorizationService(context)

    override fun getAuthRequest(): AuthorizationRequest {
        return appAuth.getAuthRequest()
    }

    override fun disposeAuthService() {
        authService.dispose()
    }

    override fun getAuthIntent(): Intent {
        val customTabsIntent = CustomTabsIntent.Builder().build()
       return authService.getAuthorizationRequestIntent(
            getAuthRequest(),
            customTabsIntent
        )
    }




}
