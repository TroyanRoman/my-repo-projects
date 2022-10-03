package com.skillbox.ascent.data.ascent.repositories.activities

import androidx.paging.*
import com.skillbox.ascent.data.ascent.api.AscentActivityApi
import com.skillbox.ascent.data.ascent.db.AscentDatabase
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityCollectData
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityUIEntity
import com.skillbox.ascent.di.preferences.UserDataPreferences
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ActivitiesRepositoryImpl @Inject constructor(
    private val ascentActivityApi: AscentActivityApi,
    @DispatcherIO val contextDispatcher: CoroutineDispatcher,
    private val userDataPrefs: UserDataPreferences,
    private val dataBase: AscentDatabase
) : ActivitiesRepository {

    private val userFullName = userDataPrefs.getRequiredUser(UserDataPreferences.FULL_NAME_KEY)
    private val userAvatar = userDataPrefs.getRequiredUser(UserDataPreferences.AVATAR_KEY)

    override suspend fun createActivity(
        activityCollectData: ActivityCollectData
    ) {
        withContext(contextDispatcher) {

            ascentActivityApi.createActivity(
                name = activityCollectData.name,
                type = activityCollectData.type,
                description = activityCollectData.desc,
                startDate = activityCollectData.startDate,
                elapsedTime = activityCollectData.time,
                distance = activityCollectData.distance
            )
        }


    }

    @OptIn(ExperimentalPagingApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override suspend fun getActivitiesList(pageSize: Int): Flow<PagingData<ActivityUIEntity>> {

        return withContext(contextDispatcher) {
            Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    prefetchDistance = pageSize,
                    initialLoadSize = pageSize * 4,
                    enablePlaceholders = false
                ), remoteMediator = ActivityRemoteMediator(
                    apiService = ascentActivityApi,
                    activityDatabase = dataBase
                )
            ) {
                  dataBase
                      .ascentActivityDao()
                      .getActivitiesFromDb()
            }
                .flow
                .map { pagingData ->
                    pagingData.map { activityData ->
                        activityData.mapToActivityUIEntity(
                            username = userFullName,
                            avatar = userAvatar
                        )
                    }
                }
        }
    }





}