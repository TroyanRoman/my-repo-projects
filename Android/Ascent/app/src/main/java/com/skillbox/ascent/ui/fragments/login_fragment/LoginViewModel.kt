package com.skillbox.ascent.ui.fragments.login_fragment

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.R
import com.skillbox.ascent.di.networking.NetworkState
import com.skillbox.ascent.di.networking.NetworkStatus
import com.skillbox.ascent.oauth_data.AuthRepository
import com.skillbox.ascent.oauth_data.AuthTokenRepository
import com.skillbox.ascent.utils.setupNetworkStateMonitoring
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.openid.appauth.TokenRequest
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authTokenRepository: AuthTokenRepository,
    private val networkState: NetworkState
) : ViewModel() {


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


    private val _isConnectedLiveData = MutableLiveData<NetworkStatus>()
    val isConnectedLiveData: LiveData<NetworkStatus>
        get() = _isConnectedLiveData

    private var _netMonitoringJob = MutableLiveData<Job?>()
    private val netMonitoringJob: LiveData<Job?>
        get() = _netMonitoringJob

    override fun onCleared() {
        super.onCleared()
        authRepository.disposeAuthService()
        cancelCheckNetworkJob()
    }


    fun onAuthCodeFailed() {
        toastEventChannel.trySendBlocking(R.string.auth_cancelled)
    }


    fun onAuthCodeReceived(tokenRequest: TokenRequest) {
        viewModelScope.launch {
            loadingMutableStateFlow.value = true
            kotlin.runCatching {
                authTokenRepository.performTokenRequest(
                    tokenRequest = tokenRequest
                )
            }.onSuccess {
                loadingMutableStateFlow.value = false
                authSuccessEventChannel.send(Unit)
            }.onFailure {
                loadingMutableStateFlow.value = false
                toastEventChannel.send(R.string.auth_cancelled)
            }
        }
    }

    fun checkConnectionsAndProceed() {
        _netMonitoringJob.value = this.setupNetworkStateMonitoring(
            networkState = networkState,
            isConnectedMutableLiveData = _isConnectedLiveData
        )
    }

    fun cancelCheckNetworkJob() {
        netMonitoringJob.value?.cancel()
    }


    fun openLoginPage() {
        openAuthPageEventChannel.trySendBlocking(authRepository.getAuthIntent())
    }


}