package com.skillbox.ascent.data.ascent.models.sport_activity.db_entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_remote_keys")
data class ActivityRemoteKeys (
    @PrimaryKey
    val activityId: Long,
    val prevKey: Int?,
    val nextKey: Int?
        )