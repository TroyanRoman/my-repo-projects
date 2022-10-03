package com.skillbox.ascent.ui.fragments.sport_activities

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityUIEntity
import com.skillbox.ascent.data.ascent.models.ui_messages.ErrorUIMessage
import com.skillbox.ascent.data.ascent.models.ui_messages.SuccessUIMessage
import com.skillbox.ascent.data.ascent.models.ui_messages.UIMessage
import com.skillbox.ascent.data.ascent.repositories.activities.ActivitiesRepository
import com.skillbox.ascent.data.ascent.repositories.primary_navigation_repo.PrimaryNavigationRepo
import com.skillbox.ascent.di.networking.NetworkStatus
import com.skillbox.ascent.di.preferences.UIMessagesPrefs
import com.skillbox.ascent.utils.SingleLiveEvent
import com.skillbox.ascent.utils.collectItems
import com.skillbox.ascent.utils.setupNetworkStateMonitoring
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val activitiesRepo: ActivitiesRepository,
    private val navRepo: PrimaryNavigationRepo,
    private val savedStateHandle: SavedStateHandle,
    private val prefs: UIMessagesPrefs,
    private val networkState: com.skillbox.ascent.di.networking.NetworkState
) : ViewModel() {

    private var _isConnected = MutableLiveData<NetworkStatus>()
    val isConnected: LiveData<NetworkStatus>
        get() = _isConnected

    val activitiesLoadSuccess = SingleLiveEvent<UIMessage>()

    private val activitiesLoadFailure = SingleLiveEvent<UIMessage>()

    private var _netMonitoringJob = MutableLiveData<Job?>()
    private val netMonitoringJob: LiveData<Job?>
        get() = _netMonitoringJob

    private val _activityUIState: MutableStateFlow<ActivityItemUIState> = MutableStateFlow(
        ActivityItemUIState.Success(
            PagingData.empty()
        )
    )
    val activityUIState: MutableStateFlow<ActivityItemUIState>
        get() = _activityUIState

    private val clearListChannel = Channel<Unit>(Channel.CONFLATED)

    @OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
    private val activitiesFlow = flowOf(
        clearListChannel.receiveAsFlow().map { PagingData.empty<ActivityUIEntity>() },
        savedStateHandle.getLiveData<String>(KEY_ACTIVITIES)
            .asFlow()
            .flatMapLatest {
                activitiesRepo.getActivitiesList(pageSize = DEFAULT_PAGE_SIZE)
            }
            .cachedIn(viewModelScope)
            .catch {
                activitiesLoadFailure.postValue(
                    ErrorUIMessage(textRes = R.string.activityLoadFailedTxt)
                )
            }
    ).flattenMerge(DEFAULT_CONCURRENCY)


    init {
        if (!savedStateHandle.contains(KEY_ACTIVITIES)) {
            savedStateHandle.set(KEY_ACTIVITIES, DEFAULT_ASCENT)
        }
        viewModelScope.launch {
            activitiesFlow.collectItems(_activityUIState)
        }
    }

    fun provideInitialNavigation(onLoginNeeded: () -> Unit) {
        navRepo.provideLoginNavigation(onLoginNeeded)
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


    fun showUIMessage(isActivityUploaded: Boolean?) {
        if (!prefs.isUIMessageShown()) {
            prefs.setIsUIMessageShown(true)
            isActivityUploaded?.let { isSuccess ->
                if (isSuccess) activitiesLoadSuccess.postValue(SuccessUIMessage(textRes = R.string.activityCreatedSuccessTxt))
                else activitiesLoadSuccess.postValue(ErrorUIMessage(R.string.activityCreateFailedTxt))
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        cancelCheckNetworkJob()
    }


    companion object {
        private const val KEY_ACTIVITIES = "key_activities"
        private const val DEFAULT_ASCENT = "androiddev"
        private const val DEFAULT_PAGE_SIZE = 30
    }

}