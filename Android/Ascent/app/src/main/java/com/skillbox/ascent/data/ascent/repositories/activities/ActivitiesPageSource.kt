package com.skillbox.ascent.data.ascent.repositories.activities

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skillbox.ascent.data.ascent.api.AscentActivityApi
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityEntity


class ActivitiesPageSource (
    private val api : AscentActivityApi,
    private val userFullName: String,
    private val avatarLink : String
        ) : PagingSource<Int, ActivityEntity>() {



    override fun getRefreshKey(state: PagingState<Int, ActivityEntity>): Int? {
        return state.anchorPosition?.let{ anchorPosition->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ActivityEntity> {
        return try {
            val page = params.key ?: INITIAL_PAGE
            val listActivityEntities = mutableListOf<ActivityEntity>()
            val responseActivity = api.getActivitiesByUser(
                after = if (params is LoadParams.Append) params.key else null,
                before = if (params is LoadParams.Prepend) params.key else null,
                pageNumber = page,
                perPage = params.loadSize
            )

            responseActivity.forEach {
                val activityEntity = ActivityEntity(
                    userFullName = userFullName,
                    avatarLink = avatarLink,
                    activity = it
                )
                listActivityEntities.add(activityEntity)
            }

            LoadResult.Page(
                data = listActivityEntities,
                prevKey = if(page == INITIAL_PAGE) null else page - 1,
                nextKey = if(listActivityEntities.isEmpty()) null else page + 1
            )
        }catch (t : Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }

    companion object {
        private const val INITIAL_PAGE = 1
    }
}

