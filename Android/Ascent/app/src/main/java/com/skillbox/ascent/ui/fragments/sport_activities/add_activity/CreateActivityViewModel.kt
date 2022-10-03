package com.skillbox.ascent.ui.fragments.sport_activities.add_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityCollectData
import com.skillbox.ascent.data.ascent.repositories.activities.ActivitiesRepository
import com.skillbox.ascent.di.networking.NetworkStatus
import com.skillbox.ascent.di.preferences.UIMessagesPrefs
import com.skillbox.ascent.utils.MyBooleanData
import com.skillbox.ascent.utils.SingleLiveEvent
import com.skillbox.ascent.utils.TimeUtil
import com.skillbox.ascent.utils.setupNetworkStateMonitoring
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateActivityViewModel @Inject constructor(
    private val activityRepo: ActivitiesRepository,

    private val prefs: UIMessagesPrefs,
    private val networkState: com.skillbox.ascent.di.networking.NetworkState
) : ViewModel() {

    private val _isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoadingLiveData

    private var _netMonitoringJob = MutableLiveData<Job?>()
    private val netMonitoringJob: LiveData<Job?>
        get() = _netMonitoringJob

    val isSuccess = SingleLiveEvent<MyBooleanData>()

    private val _wrongTimeSetData = MutableLiveData<Boolean>()
    val wrongTimeSetData: LiveData<Boolean>
        get() = _wrongTimeSetData

    private var _isConnected = MutableLiveData<NetworkStatus>()
    val isConnected: LiveData<NetworkStatus>
        get() = _isConnected
    

    fun checkNetworkConnections() {
        _netMonitoringJob.value = this.setupNetworkStateMonitoring(
            networkState = networkState,
            isConnectedMutableLiveData = _isConnected
        )
    }

    fun cancelCheckNetworkJob() {
        netMonitoringJob.value?.cancel()
    }

    fun createActivity(
        activityCollectData: ActivityCollectData,
    ) {
        prefs.setIsUIMessageShown(false)
        _isLoadingLiveData.postValue(true)
        viewModelScope.launch {
            try {
                activityRepo.createActivity(activityCollectData)
                _isLoadingLiveData.postValue(false)
                isSuccess.postValue(MyBooleanData(isDataUploaded = true))
            } catch (t: Throwable) {
                _isLoadingLiveData.postValue(false)
                isSuccess.postValue(MyBooleanData(isDataUploaded = false))
                t.printStackTrace()
            } finally {
                _isLoadingLiveData.postValue(false)
            }
        }
    }

    fun calculateElapsingTime(hoursDiff: Int, minutesDiff: Int): Int {
        return try {
            _wrongTimeSetData.postValue(false)
            TimeUtil.calculateElapsingTime(hoursDiff, minutesDiff)
        } catch (e: IllegalArgumentException) {
            _wrongTimeSetData.postValue(true)
            0
        }
    }


    fun setCreateTimeString(hoursDiff: Int, minutesDiff: Int): String {
        return try {
            TimeUtil.setCreateTimeString(hoursDiff, minutesDiff)
        } catch (e: IllegalArgumentException) {
            ""
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelCheckNetworkJob()
    }
}