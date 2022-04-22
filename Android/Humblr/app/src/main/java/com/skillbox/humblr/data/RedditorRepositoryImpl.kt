package com.skillbox.humblr.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.skillbox.humblr.db.RedditDataBase
import com.skillbox.humblr.db.RedditPostRemoteMediator
import com.skillbox.humblr.di.DispatcherIO
import com.skillbox.humblr.networking.RedditApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RedditorRepositoryImpl @Inject constructor(
    val redditApi: RedditApi,
    val dataBase: RedditDataBase,
    @DispatcherIO val contextDispatcher : CoroutineDispatcher

) : RedditorRepository {

    override suspend fun getRedditor(redditorName: String): RedditItem.Redditor {
        return withContext(contextDispatcher) {
            redditApi.getRedditorInfo(redditorName = redditorName).dataResponse
        }
    }

    @ExperimentalPagingApi
    override suspend fun getRedditorSubs(
        redditorName: String,
        pageSize: Int
    ): Flow<PagingData<RedditItem.RedditPost>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                prefetchDistance = pageSize,
                initialLoadSize = pageSize * 3,
                maxSize = pageSize * 4,
                enablePlaceholders = false
            ),
            remoteMediator = RedditPostRemoteMediator(
                redditApi = redditApi,
                dataBase = dataBase,
                postsCategory = PostsType.REDDITOR_SUBS,
                authorName = redditorName
            )
        ) {
            dataBase.redditPostDao().getPosts(PostsType.REDDITOR_SUBS.name)
        }.flow
    }


    override suspend fun makeFriend(redditorName: String) {
        withContext(contextDispatcher) {
            redditApi.makeFriend(
                redditorName = redditorName,
                username = FriendMade(name = redditorName, null)
            )
        }

    }

    override suspend fun unFriend(redditorName: String) {
        withContext(contextDispatcher) {
            redditApi.unFriend(redditorName)
        }
    }

    override suspend fun getFriendRelation(redditorName: String): Boolean {
        return withContext(contextDispatcher) {
            redditApi.getRedditorInfo(redditorName).dataResponse.isFriend
        }
    }

}