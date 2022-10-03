package com.skillbox.ascent.data.ascent.models.sport_activity

import android.os.Parcelable
import com.skillbox.ascent.data.ascent.models.sport_activity.db_entities.ActivityData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*

//для парсинга данных из сети
@Parcelize
@JsonClass(generateAdapter = true)
data class AscentActivity(
    val id: Long,
    val distance: Float,
    val name: String,
    val type: String,

    @Json(name = "total_elevation_gain")
    val elevation: Float? = 0f,

    @Json(name = "elapsed_time")
    val time: Int,

    @Json(name = "description")
    val desc: String? = "",

    @Json(name = "start_date_local")
    val startDate: Date
) : Parcelable {

    fun mapToActivityData(): ActivityData = ActivityData(
        id = this.id,
        distance = this.distance,
        name = this.name,
        type = this.type,
        elevation = this.elevation,
        time = this.time,
        desc = this.desc,
        startDate = this.startDate
    )
}
