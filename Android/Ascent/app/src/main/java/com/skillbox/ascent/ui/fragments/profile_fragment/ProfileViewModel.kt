package com.skillbox.ascent.ui.fragments.profile_fragment

import androidx.lifecycle.*
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.ui_messages.ErrorUIMessage
import com.skillbox.ascent.data.ascent.models.ui_messages.SuccessUIMessage
import com.skillbox.ascent.data.ascent.models.ui_messages.UIMessage
import com.skillbox.ascent.data.ascent.repositories.user.UserRepository
import com.skillbox.ascent.di.networking.NetworkState
import com.skillbox.ascent.di.networking.NetworkStatus
import com.skillbox.ascent.oauth_data.AuthTokenRepository
import com.skillbox.ascent.utils.setupNetworkStateMonitoring
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo: UserRepository,
    private val authTokenrepo: AuthTokenRepository,
    private val networkState: NetworkState,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {


    private val _profileUIStateFlow: MutableStateFlow<ProfileUIState> = MutableStateFlow(
        ProfileUIState.Loading(true)
    )
    val profileUIState: MutableStateFlow<ProfileUIState>
        get() = _profileUIStateFlow

    private val _updateMsg = MutableLiveData<UIMessage>()
    val updateMessage: LiveData<UIMessage>
        get() = _updateMsg

    private var _isConnected = MutableLiveData<NetworkStatus>()
    val isConnected: LiveData<NetworkStatus>
        get() = _isConnected

    private var _netMonitoringJob = MutableLiveData<Job?>()
    private val netMonitoringJob: LiveData<Job?>
        get() = _netMonitoringJob

    @OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
    val stateProfile = flowOf(
        savedStateHandle.getLiveData<String>(KEY_PROFILE)
            .asFlow()
            .flatMapLatest {
                userRepo.getUserIdentity()
            }
    ).flattenMerge(DEFAULT_CONCURRENCY)

    init {
        if (!savedStateHandle.contains(KEY_PROFILE)) {
            savedStateHandle.set(
                KEY_PROFILE,
                DEFAULT_ASCENT
            )
        }
        checkProfile()
    }


    fun checkNetworkConnections() {
        _netMonitoringJob.value = this.setupNetworkStateMonitoring(
            networkState = networkState,
            isConnectedMutableLiveData = _isConnected
        )
    }

    fun cancelCheckNetworkJob() {
        netMonitoringJob.value?.cancel()
    }

    fun showNotificationMessage() {
        viewModelScope.launch {
            try {
                userRepo.showNotificationMessage()
            } catch (t: Throwable) {
                Timber.tag("LogoutError").d("notification error = $t")
            }
        }
    }

    fun logoutCurrentUser(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                authTokenrepo.logoutCurrentUser()
                authTokenrepo.corruptAccessToken()
                onLogoutSuccess()
            } catch (t: Throwable) {
                Timber.tag("LogoutError").d("logout error = $t")
            }
        }
    }


    fun checkProfile() {
        _profileUIStateFlow.value = ProfileUIState.Loading(true)
        viewModelScope.launch {
            try {
                stateProfile.collectLatest { profile ->
                    _profileUIStateFlow.value = ProfileUIState.Success(profile)
                    userRepo.storeUserData(profile)
                }
            } catch (e: UnknownHostException) {
                //no network - unknownhost exception
                _profileUIStateFlow.value = ProfileUIState.Error(R.string.no_internet)
            } catch (t: Throwable) {
                //no vpn ->
                _profileUIStateFlow.value = ProfileUIState.Error(R.string.no_vpn_status)
            }

        }
    }


    fun updateUser(weight: Float, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                userRepo.updateUserIdentity(weight)
                _updateMsg.postValue(SuccessUIMessage(R.string.success_weight_update_txt))
                onSuccess()
            } catch (t: Throwable) {
                Timber.tag("UpdateUserIdentityError").d("User Identity update error = $t")
                _updateMsg.postValue(ErrorUIMessage(R.string.error_weight_update_txt))
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        cancelCheckNetworkJob()
    }

    companion object {
        private const val KEY_PROFILE = "key_profile"
        private const val DEFAULT_ASCENT = "androiddevprofile"
    }


}