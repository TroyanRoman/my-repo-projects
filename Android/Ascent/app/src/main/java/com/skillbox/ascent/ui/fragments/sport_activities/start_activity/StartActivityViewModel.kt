package com.skillbox.ascent.ui.fragments.sport_activities.start_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.skillbox.ascent.data.ascent.models.sport_activity.TrackerState
import com.skillbox.ascent.data.ascent.repositories.activities.StartActivityRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StartActivityViewModel @Inject constructor(
    private val serviceRepo: StartActivityRepo
) : ViewModel() {

    private var _distanceLiveData = serviceRepo.distanceLiveData
    val distanceLiveData: LiveData<Double>
        get() = _distanceLiveData

    private var _timeLiveData = serviceRepo.timeLiveData
    val timeLiveData: LiveData<Long>
        get() = _timeLiveData

    private var _trackerUIStateLiveData = serviceRepo.trackerState
    val trackerUIState: LiveData<TrackerState>
        get() = _trackerUIStateLiveData


}