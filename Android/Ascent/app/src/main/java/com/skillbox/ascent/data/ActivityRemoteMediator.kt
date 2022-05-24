package com.skillbox.ascent.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.skillbox.ascent.data.ascent.api.AscentActivityApi
import com.skillbox.ascent.data.ascent.db.AscentDatabase
import com.skillbox.ascent.data.ascent.models.sport_activity.db_entities.ActivityRemoteKeys
import com.skillbox.ascent.data.ascent.models.sport_activity.AscentActivity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ActivityRemoteMediator(
    private val apiService: AscentActivityApi,
    private val activityDatabase: AscentDatabase
) : RemoteMediator<Int, AscentActivity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AscentActivity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }
        try {
            val activitiesList =
                apiService.getActivitiesByUser(pageNumber = page, perPage = state.config.pageSize)
            val endOfPaginationReached = activitiesList.isEmpty()

            activityDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    activityDatabase.ascentActivityRemoteKeyDao().clearRemoteKeys()
                    activityDatabase.ascentActivityDao().clearActivities()
                }
                val prevKey = if (page == INITIAL_PAGE) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = activitiesList.map {
                    ActivityRemoteKeys(activityId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                Log.d("Database", "activity remote keys = $keys")

                activityDatabase.ascentActivityRemoteKeyDao().insertAll(keys)
                Log.d("Database", "activity list = $activitiesList")

                activityDatabase.ascentActivityDao().insertActivities(activitiesList)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            exception.printStackTrace()
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            exception.printStackTrace()
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, AscentActivity>): ActivityRemoteKeys? {
        return state.pages.firstOrNull() {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let{ activityEntity->
           activityDatabase.ascentActivityRemoteKeyDao().remoteKeysActivityId(activityEntity.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, AscentActivity>): ActivityRemoteKeys? {
        return state.pages.lastOrNull() {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let{ activityEntity->
            activityDatabase.ascentActivityRemoteKeyDao().remoteKeysActivityId(activityEntity.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, AscentActivity>) : ActivityRemoteKeys? {
        return state.anchorPosition?.let { position->
            state.closestItemToPosition(position)?.id.let { activityId ->
                activityDatabase.ascentActivityRemoteKeyDao().remoteKeysActivityId(activityId!!)
            }
        }
    }


    companion object {
        private const val INITIAL_PAGE = 1
    }

}