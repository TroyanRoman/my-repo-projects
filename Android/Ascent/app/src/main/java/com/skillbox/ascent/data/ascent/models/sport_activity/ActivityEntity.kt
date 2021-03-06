package com.skillbox.ascent.data.ascent.models.sport_activity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//для отображения в ресайклере
@Parcelize
data class ActivityEntity(
    val userFullName: String,
    val avatarLink : String,
    val activity: AscentActivity
): Parcelable
