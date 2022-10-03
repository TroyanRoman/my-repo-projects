package com.skillbox.ascent.data.ascent.repositories.activities

import androidx.lifecycle.MutableLiveData
import com.skillbox.ascent.data.ascent.models.sport_activity.TrackerState

interface StartActivityRepo {
    val distanceLiveData : MutableLiveData<Double>
    val timeLiveData : MutableLiveData<Long>
    val trackerState : MutableLiveData<TrackerState>
}