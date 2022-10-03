package com.skillbox.ascent.data.ascent.db.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skillbox.ascent.data.ascent.db.DatabaseContract
import com.skillbox.ascent.data.ascent.models.sport_activity.db_entities.ActivityData

@Dao
interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     suspend fun insertActivities(activities : List<ActivityData>)

    @Query("SELECT * FROM ${DatabaseContract.ActivityContract.TABLE_NAME} ORDER BY ${DatabaseContract.ActivityContract.Columns.START_DATE} DESC ")
    fun getActivitiesFromDb() : PagingSource<Int, ActivityData>

    @Query("DELETE FROM ${DatabaseContract.ActivityContract.TABLE_NAME} ")
    suspend fun clearActivities()

    @Query("SELECT ${DatabaseContract.ActivityContract.Columns.START_DATE} FROM ${DatabaseContract.ActivityContract.TABLE_NAME} ORDER BY ${DatabaseContract.ActivityContract.Columns.START_DATE} DESC")
    suspend fun getDateList() : List<Long>

}