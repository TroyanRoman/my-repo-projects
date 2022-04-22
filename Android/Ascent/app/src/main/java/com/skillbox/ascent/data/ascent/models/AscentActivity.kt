package com.skillbox.ascent.data.ascent.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*
//для парсинга данных из сети
@Parcelize
@JsonClass(generateAdapter = true)
data class AscentActivity(
    val id : Long? = null,
    val distance : Float,
    val name : String,
    val type : String,
    @Json(name = "total_elevation_gain")
    val elevation: Int? = 0,
    @Json(name = "elapsed_time")
    val time : Int,
    @Json(name = "description")
    val desc : String? = "",
    @Json(name = "start_date_local")
    val startDate: Date
) : Parcelable
