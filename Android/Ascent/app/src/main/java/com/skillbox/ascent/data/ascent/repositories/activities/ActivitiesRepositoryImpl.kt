package com.skillbox.ascent.data.ascent.repositories.activities

import android.util.Log
import androidx.paging.*
import com.skillbox.ascent.data.ActivityRemoteMediator
import com.skillbox.ascent.data.ascent.api.AscentActivityApi
import com.skillbox.ascent.data.ascent.db.AscentDatabase
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityEntity
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityModel
import com.skillbox.ascent.di.UserDataPreferences
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import com.skillbox.ascent.oauth_data.AuthConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ActivitiesRepositoryImpl @Inject constructor(
    private val ascentActivityApi: AscentActivityApi,
    @DispatcherIO val contextDispatcher: CoroutineDispatcher,
    private val userDataPrefs: UserDataPreferences,
    private val dataBase: AscentDatabase
) : ActivitiesRepository {


    private val userFullName = userDataPrefs.getRequiredUser(AuthConfig.FULL_NAME_KEY)
    private val userAvatar = userDataPrefs.getRequiredUser(AuthConfig.AVATAR_KEY)
    private val userId = userDataPrefs.getRequiredUserId(ID_USER_KEY)



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

    @OptIn(ExperimentalPagingApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override suspend fun getActivitiesList(pageSize: Int): Flow<PagingData<ActivityEntity>> {
        Log.d("ActivityLog", "ascent activity userid = $userId")

        return withContext(contextDispatcher){
            Log.d("ThreadLog"," current thread = ${Thread.currentThread()}")
            Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = pageSize,
                initialLoadSize = pageSize * 3,
                maxSize = pageSize * 4,
                enablePlaceholders = false
            ), remoteMediator = ActivityRemoteMediator(
                apiService = ascentActivityApi,
                activityDatabase = dataBase
            )
        )
        { dataBase.ascentActivityDao().getActivitiesFromDb() }.flow.mapLatest {pagingData->
            pagingData.map { ascentActivity ->
                ActivityEntity(
                    userFullName = userFullName,
                    avatarLink = userAvatar,
                    activity = ascentActivity
                )
            }
        }}
        /*
        {dataBase.ascentActivityDao().getActivitiesFromDb(userId)}.flow.flatMapLatest {

            Log.d("ActivityLog", "ascent activity userid = $userId")
           flowOf( it.map { ascentActivity->
               Log.d("ActivityLog", "ascent activity = $ascentActivity")
              val entity =  ActivityEntity(
                    userFullName = userFullName,
                    avatarLink = userAvatar,
                    activity = ascentActivity
                )
               Log.d("ActivityLog", "ascent activity entity = $entity")
               entity


            })
        }

         */
    }


    /*   override suspend fun getActivitiesList(
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

     */

    companion object {
        private const val ID_USER_KEY = "user_id_key"
    }
}