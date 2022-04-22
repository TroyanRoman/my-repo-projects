package com.skillbox.ascent.data.ascent.repositories.activities

import androidx.paging.PagingData
import com.skillbox.ascent.data.ascent.models.ActivityEntity
import com.skillbox.ascent.data.ascent.models.ActivityModel
import com.skillbox.ascent.data.ascent.models.AscentActivity
import com.skillbox.ascent.data.ascent.models.AscentUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

interface ActivitiesRepository {

    suspend fun createActivity(activityModel: ActivityModel)

    suspend fun getActivitiesList(pageSize: Int, ): Flow<PagingData<ActivityEntity>>
}