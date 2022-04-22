package com.skillbox.humblr.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.skillbox.humblr.data.RedditItem
import com.skillbox.humblr.db.DataBaseContract
@Dao
interface RedditorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertRedditors(redditor : List<RedditItem.Redditor>)

    @Transaction
    @Query("SELECT * FROM ${DataBaseContract.RedditorContract.TABLE_NAME}")
     fun getAllRedditorsWithRedditorInfo() : PagingSource<Int, RedditItem.Redditor>

    @Query("DELETE FROM ${DataBaseContract.RedditorContract.TABLE_NAME}")
    fun cleanRedditors()
}