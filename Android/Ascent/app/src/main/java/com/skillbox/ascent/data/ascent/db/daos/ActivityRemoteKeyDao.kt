package com.skillbox.ascent.data.ascent.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.skillbox.ascent.data.ascent.models.sport_activity.db_entities.ActivityRemoteKeys
@Dao
interface ActivityRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey : List<ActivityRemoteKeys>)

    @Query("SELECT * FROM activity_remote_keys WHERE activityId = :activityId")
    suspend fun remoteKeysActivityId(activityId : Long) : ActivityRemoteKeys?

    @Query("DELETE FROM activity_remote_keys")
    suspend fun clearRemoteKeys()
}