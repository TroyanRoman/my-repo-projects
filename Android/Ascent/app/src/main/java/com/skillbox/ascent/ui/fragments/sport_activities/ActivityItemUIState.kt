package com.skillbox.ascent.ui.fragments.sport_activities

import androidx.paging.PagingData
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityEntity

sealed class ActivityItemUIState {
    data class Success(val itemPagingData: PagingData<ActivityEntity>?) : ActivityItemUIState()
    data class Error(val exception: Throwable) : ActivityItemUIState()
}
