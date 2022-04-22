package com.skillbox.humblr.ui.fragments.redditor

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skillbox.humblr.data.RedditItem
import com.skillbox.humblr.networking.RedditApi
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class RedditorPagingSource(
    private val redditApi: RedditApi,
    private val author: String
) : PagingSource<String, RedditItem.RedditPost>() {

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditItem.RedditPost> {
        return try {

            val dataResponse = redditApi.getRedditorSubs(
                after = if (params is LoadParams.Append) params.key else null,
                before = if (params is LoadParams.Prepend) params.key else null,
                limit = params.loadSize,
                author = author
            ).dataResponse

            val response = redditApi.getSubscribes()
          //  response.dataResponse.children.map {
           //     it.redditPost.subredditId!!
           // }
            val listOfSubscribed = response.dataResponse.children.map {
                it.redditItem.subredditId
            }

            val subredditsList = dataResponse.children.map {
                it.redditItem
            }.also {
                it.map { redditPost ->
                    redditPost.isSubscribed = listOfSubscribed.contains(redditPost.subredditId)
                }
            }

            LoadResult.Page(
                data = subredditsList,
                nextKey = dataResponse.after,
                prevKey = dataResponse.before
            )
        } catch (e: IOException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Timber.tag("LoadPageError").e("LoadPageError = $e")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, RedditItem.RedditPost>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }
}