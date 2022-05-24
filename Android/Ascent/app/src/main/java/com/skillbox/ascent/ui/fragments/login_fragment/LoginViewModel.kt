package com.skillbox.ascent.ui.fragments.login_fragment

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.R
import kotlinx.coroutines.channels.Channel
import com.skillbox.ascent.oauth_data.AuthRepository

import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authService: AuthorizationService
) : ViewModel() {

   // private val authService = authRepository.getAuthService()
    
    private val openAuthPageEventChannel = Channel<Intent>(Channel.BUFFERED)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val authSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)

    private val loadingMutableStateFlow = MutableStateFlow(false)

    val openAuthPageFlow: Flow<Intent>
        get() = openAuthPageEventChannel.receiveAsFlow()

    val loadingFlow: Flow<Boolean>
        get() = loadingMutableStateFlow.asStateFlow()

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val authSuccessFlow: Flow<Unit>
        get() = authSuccessEventChannel.receiveAsFlow()

    override fun onCleared() {
        super.onCleared()
        authService.dispose()
    }

    fun onAuthCodeFailed(exception: AuthorizationException) {
       toastEventChannel.trySendBlocking(R.string.auth_cancelled)
    }


    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            loadingMutableStateFlow.value = true
            kotlin.runCatching {
                authRepository.performTokenRequest(
                    authService = authService,
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
                loadingMutableStateFlow.value = false
                Log.d("Auth", "on auth code received model = $it")
                authSuccessEventChannel.send(Unit)
            }.onFailure {
                loadingMutableStateFlow.value = false
                toastEventChannel.send(R.string.auth_cancelled)
            }
        }
    }



    fun openLoginPage() {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        Log.d("Auth","openloginpage in login VM started")
        val openAuthPageIntent = authService.getAuthorizationRequestIntent(
            authRepository.getAuthRequest(),
            customTabsIntent
        )

        openAuthPageEventChannel.trySendBlocking(openAuthPageIntent)
    }
}