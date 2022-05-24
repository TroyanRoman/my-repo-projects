package com.skillbox.ascent.data.ascent.repositories.activities

import androidx.paging.PagingData
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityEntity
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityModel
import kotlinx.coroutines.flow.Flow

interface ActivitiesRepository {

    suspend fun createActivity(activityModel: ActivityModel)

    suspend fun getActivitiesList(pageSize: Int, ): Flow<PagingData<ActivityEntity>>
}