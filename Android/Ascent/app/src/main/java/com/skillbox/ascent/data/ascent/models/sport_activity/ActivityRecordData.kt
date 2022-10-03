package com.skillbox.ascent.data.ascent.models.sport_activity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class ActivityRecordData(
    val distance : String,
    val startDate : Date,
    val startTime : String,
    val time : String
) : Parcelable
