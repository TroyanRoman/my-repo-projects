package com.skillbox.ascent.data.ascent.repositories.activities

import androidx.lifecycle.MutableLiveData
import com.skillbox.ascent.data.ascent.models.sport_activity.TrackerState
import javax.inject.Inject

class StartActivityRepoImpl @Inject constructor() : StartActivityRepo {

    override val distanceLiveData = MutableLiveData<Double>()
    override val timeLiveData = MutableLiveData<Long>()
    override val trackerState = MutableLiveData<TrackerState>()


}