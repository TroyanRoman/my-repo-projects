package com.example.yourdrive.presentation.authorization.data

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import com.example.yourdrive.presentation.authorization.AppAuth
import kotlinx.coroutines.CoroutineDispatcher
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService

interface AuthorizationRepository {
//todo think about renaming current interface

    fun disposeAuthService()

    fun getAuthIntent() : Intent

    class Base(
        private val authorizationService: AuthorizationService,
        private val appAuth: AppAuth
    ) : AuthorizationRepository {

        override fun disposeAuthService() = authorizationService.dispose()

        override fun getAuthIntent(): Intent {
            return authorizationService.getAuthorizationRequestIntent(
                appAuth.getAuthRequest(),
                CustomTabsIntent.Builder().build()
            )
        }
    }
}