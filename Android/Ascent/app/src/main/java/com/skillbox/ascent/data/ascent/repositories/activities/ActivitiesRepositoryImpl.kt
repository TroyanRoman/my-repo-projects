package com.skillbox.ascent.data.ascent.repositories.activities

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.skillbox.ascent.data.ascent.api.AscentActivityApi
import com.skillbox.ascent.data.ascent.api.AscentApi
import com.skillbox.ascent.data.ascent.models.ActivityEntity
import com.skillbox.ascent.data.ascent.models.ActivityModel
import com.skillbox.ascent.data.ascent.models.AscentActivity
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.di.UserDataPreferences
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import com.skillbox.ascent.oauth_data.AuthConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class ActivitiesRepositoryImpl @Inject constructor(
    private val ascentActivityApi: AscentActivityApi,
    @DispatcherIO val contextDispatcher: CoroutineDispatcher,
    private val userDataPrefs: UserDataPreferences
) : ActivitiesRepository {




    private val userFullName = userDataPrefs.getRequiredUser(AuthConfig.FULL_NAME_KEY)
    private val userAvatar = userDataPrefs.getRequiredUser(AuthConfig.AVATAR_KEY)

    override suspend fun createActivity(
        activityModel: ActivityModel
    ) {
        withContext(contextDispatcher) {

            ascentActivityApi.createActivity(
                name = activityModel.name,
                type = activityModel.type,
                description = activityModel.desc,
                startDate = activityModel.startDate,
                elapsedTime = activityModel.time,
                distance = activityModel.distance
            )
        }

    }


    override suspend fun getActivitiesList(
        pageSize: Int,
       // userFullName : String,
       // avatarLink: String
    ): Flow<PagingData<ActivityEntity>> {
        Log.d("GetActivity","get activity list in repo")
        return withContext(contextDispatcher) {
            Log.d("GetActivity","get activity list in repo")
            Pager(
                config = PagingConfig(
                    pageSize = pageSize
                ), pagingSourceFactory = {
                    ActivitiesPageSource(
                        api = ascentActivityApi,
                        userFullName = userFullName,
                        avatarLink = userAvatar)
                }
            ).flow
        }
    }
}