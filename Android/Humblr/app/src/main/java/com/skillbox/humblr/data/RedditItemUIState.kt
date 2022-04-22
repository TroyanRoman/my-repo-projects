package com.skillbox.humblr.data

import androidx.paging.PagingData
import java.lang.Exception

sealed class RedditItemUIState{
    data class Success<T : RedditItem>(val itemPagingData: PagingData<T>?) : RedditItemUIState()
    data class Error(val exception: Throwable) : RedditItemUIState()
}
