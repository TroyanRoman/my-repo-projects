package com.skillbox.ascent.oauth_data

import com.skillbox.ascent.di.AppAuth
import com.skillbox.ascent.di.AuthTokenPreference
import com.skillbox.ascent.di.networking.AuthFailedInterceptorQualifier
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationService
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@AuthFailedInterceptorQualifier
class AuthFailedInterceptor @Inject constructor(
    private val appAuth: AppAuth,
    private val authPrefs: AuthTokenPreference,
    private val authService : AuthorizationService
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequestTime = System.currentTimeMillis()
        val originalResponse = chain.proceed(chain.request())
        return originalResponse
            .takeIf { it.code != 401 }
            ?: handleUnauthorizedResponse(chain, originalResponse, originalRequestTime)

    }

    private fun handleUnauthorizedResponse(
        chain: Interceptor.Chain,
        originalResponse: Response,
        requestTimestamp: Long
    ): Response {
        val latch = getLatch()
        return when {
            latch != null && latch.count > 0 -> handleTokenIsUpdating(
                chain,
                latch,
                requestTimestamp
            )
                ?: originalResponse
            tokenUpdateTime > requestTimestamp -> updateTokenAndProceedChain(chain)
            else -> handleTokenNeedRefresh(chain) ?: originalResponse
        }

    }

    private fun handleTokenIsUpdating(
        chain: Interceptor.Chain,
        latch: CountDownLatch,
        requestTimestamp: Long
    ): Response? {
        return if (latch.await(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            && tokenUpdateTime > requestTimestamp
        ) {
            updateTokenAndProceedChain(chain)
        } else {
            null
        }
    }

    private fun updateTokenAndProceedChain(chain: Interceptor.Chain): Response {
        val newRequest = updateOriginalCallWithNewToken(chain.request())
        return chain.proceed(newRequest)
    }

    private fun handleTokenNeedRefresh(
        chain: Interceptor.Chain
    ): Response? {
        return if (refreshedToken()) {
            updateTokenAndProceedChain(chain)
        } else {
            null
        }
    }

    private fun refreshedToken(): Boolean {
        initLatch()

        val tokenRefreshed = runBlocking {
            kotlin.runCatching {
                val refreshToken = authPrefs.getRequiredToken(AuthConfig.REFRESH_PREF_KEY)
                val refreshRequest = appAuth.getRefreshRequest(refreshToken)
                appAuth.performTokenRequest(
                    authService,
                    refreshRequest
                )
            }
                .getOrNull()
                ?.let { tokenModel ->
                    authPrefs.setStoredData(tokenModel)
                    Timber.tag("RefreshedToken").d("storing data into prefs = $tokenModel")
                    true
                } ?: false
        }

        if (tokenRefreshed) {
            tokenUpdateTime = System.currentTimeMillis()
        } else {
            Timber.d("Logout after refresh failed")
        }
        getLatch()?.countDown()
        return tokenRefreshed
    }

    private fun updateOriginalCallWithNewToken(request: Request): Request {
        val accessToken = authPrefs.getRequiredToken(AuthConfig.ACCESS_PREF_KEY)
        return accessToken.let { newAccessToken ->
            request
                .newBuilder()
                .header("Authorization", newAccessToken)
                .build()
        }
    }

    companion object {
        private const val REQUEST_TIMEOUT = 30L

        @Volatile
        private var tokenUpdateTime: Long = 0L

        private var countDownLatch: CountDownLatch? = null

        @Synchronized
        fun initLatch() {
            countDownLatch = CountDownLatch(1)
        }

        @Synchronized
        fun getLatch() = countDownLatch
    }
}