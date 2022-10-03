package com.skillbox.ascent.data.ascent.models.sport_activity

sealed class TrackerState {
    object START : TrackerState()
    object PAUSE : TrackerState()
    object END : TrackerState()
}
