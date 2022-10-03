package com.skillbox.ascent.data.ascent.repositories.activities

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.skillbox.ascent.data.ascent.api.AscentActivityApi
import com.skillbox.ascent.data.ascent.db.AscentDatabase
import com.skillbox.ascent.data.ascent.models.sport_activity.db_entities.ActivityData
import com.skillbox.ascent.data.ascent.models.sport_activity.db_entities.ActivityRemoteKeys
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ActivityRemoteMediator(
    private val apiService: AscentActivityApi,
    private val activityDatabase: AscentDatabase
) : RemoteMediator<Int, ActivityData>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ActivityData>
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
                    .map { networkEntity ->
                        networkEntity.mapToActivityData()
                    }

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
                activityDatabase.ascentActivityRemoteKeyDao().insertAll(keys)
                activityDatabase.ascentActivityDao().insertActivities(activitiesList)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ActivityData>): ActivityRemoteKeys? {
        return state.pages.firstOrNull() {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { activityData ->
            activityDatabase.ascentActivityRemoteKeyDao().remoteKeysActivityId(activityData.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ActivityData>): ActivityRemoteKeys? {
        return state.pages.lastOrNull() {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { activityData ->
            activityDatabase.ascentActivityRemoteKeyDao().remoteKeysActivityId(activityData.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ActivityData>): ActivityRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { activityId ->
                activityDatabase.ascentActivityRemoteKeyDao().remoteKeysActivityId(activityId)
            }
        }
    }


    companion object {
        private const val INITIAL_PAGE = 1
    }

}