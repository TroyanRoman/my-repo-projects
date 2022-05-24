package com.skillbox.ascent.data.ascent.models.sport_activity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.skillbox.ascent.data.ascent.db.DatabaseContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.util.*
//для парсинга данных из сети
@Parcelize
@JsonClass(generateAdapter = true)
@Entity(
    tableName = DatabaseContract.ActivityContract.TABLE_NAME,

)
data class AscentActivity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.ID)
    val id : Long ,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.DISTANCE)
    val distance : Float,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.NAME)
    val name : String,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.TYPE)
    val type : String,

    @Json(name = "total_elevation_gain")
    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.ELEVATION)
    val elevation: Float? = 0f,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.TIME)
    @Json(name = "elapsed_time")
    val time : Int,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.DESC)
    @Json(name = "description")
    val desc : String? = "",

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.START_DATE)
    @Json(name = "start_date_local")
    val startDate: Date
) : Parcelable
