package com.skillbox.ascent.data.ascent.models.sport_activity

import android.os.Parcelable
import com.skillbox.ascent.data.ascent.models.sport_activity.db_entities.ActivityData
import kotlinx.parcelize.Parcelize

//для отображения в ресайклере
@Parcelize
data class ActivityUIEntity(
    val userFullName: String,
    val avatarLink : String,
    val activity: ActivityData
): Parcelable
