package com.skillbox.humblr.ui.fragments.main_fragment.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skillbox.humblr.data.ItemChildrenDataWrapper
import com.skillbox.humblr.data.PostsType
import com.skillbox.humblr.data.RedditItem
import com.skillbox.humblr.data.RedditItemsWrapper
import com.skillbox.humblr.networking.RedditApi
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class NewsPageSource(
    private val redditApi: RedditApi,
    private  val apiResponse: ItemChildrenDataWrapper<RedditItemsWrapper<RedditItem.RedditPost>>
) : PagingSource<String, RedditItem.RedditPost>() {



    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditItem.RedditPost> {
    return try {

        val userName = redditApi.getUserIdentity().name
        val subscribedResponse = redditApi.getSavedSubs(userName = userName)

        val listOfSubscribed = subscribedResponse.dataResponse.children.map {
            it.redditItem.name
        }


        val subredditsList = apiResponse.children.map {
            it.redditItem
        }.also { subredditsList ->
            subredditsList.map { redditPost ->
                redditPost.isSubscribed = listOfSubscribed.contains(redditPost.name)
            }
        }
        LoadResult.Page(
            data = subredditsList,
            nextKey = apiResponse.after,
            prevKey = apiResponse.before
        )
    }catch (e: IOException) {
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