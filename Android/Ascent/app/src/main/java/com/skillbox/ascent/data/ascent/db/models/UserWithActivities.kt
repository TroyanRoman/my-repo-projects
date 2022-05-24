package com.skillbox.ascent.data.ascent.db.models

import androidx.room.Embedded
import androidx.room.Relation
import com.skillbox.ascent.data.ascent.db.DatabaseContract
import com.skillbox.ascent.data.ascent.models.sport_activity.AscentActivity
import com.skillbox.ascent.data.ascent.models.AscentUser

data class UserWithActivities (
    @Embedded
    val user: AscentUser,
    @Relation(
        parentColumn = DatabaseContract.UserContract.Columns.ID,
        entityColumn = DatabaseContract.ActivityContract.Columns.PARENT_ID
    )
    val activities: List<AscentActivity>
        )