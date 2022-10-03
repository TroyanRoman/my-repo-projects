package com.skillbox.ascent.data.ascent.repositories.activities

import androidx.paging.PagingData
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityUIEntity
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityCollectData
import kotlinx.coroutines.flow.Flow

interface ActivitiesRepository {

    suspend fun createActivity(activityCollectData: ActivityCollectData)

    suspend fun getActivitiesList(pageSize: Int): Flow<PagingData<ActivityUIEntity>>
}