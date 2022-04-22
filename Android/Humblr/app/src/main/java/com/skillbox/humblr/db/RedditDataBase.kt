package com.skillbox.humblr.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skillbox.humblr.data.RedditItem
import com.skillbox.humblr.db.dao.RedditPostDao
import com.skillbox.humblr.db.dao.RedditPostKeysDao
import com.skillbox.humblr.db.dao.RedditorDao
import com.skillbox.humblr.db.dao.RedditorKeysDao


@Database(
    entities = [RedditItem.RedditPost::class, RedditItem.Redditor::class, RemotePostKeys::class, RemoteRedditorKeys::class],
    version = RedditDataBase.DB_VERSION,
    exportSchema = false
)
abstract class RedditDataBase  : RoomDatabase(){

    abstract fun redditPostDao() : RedditPostDao
    abstract fun redditorDao() : RedditorDao
    abstract fun redditPostRemoteKeyDao() : RedditPostKeysDao
    abstract fun redditorRemoteKeyDao() : RedditorKeysDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "reddit_database"
    }
}