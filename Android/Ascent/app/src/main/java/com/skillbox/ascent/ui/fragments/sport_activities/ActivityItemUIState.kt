package com.skillbox.ascent.ui.fragments.sport_activities

import androidx.paging.PagingData
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityUIEntity

sealed class ActivityItemUIState {
    data class Success(val itemPagingData: PagingData<ActivityUIEntity>?) : ActivityItemUIState()
    data class Error(val exception: Throwable) : ActivityItemUIState()
}
