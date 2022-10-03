package com.skillbox.ascent.data.ascent.models.sport_activity.db_entities

import android.os.Parcelable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skillbox.ascent.data.ascent.db.DatabaseContract
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityUIEntity
import kotlinx.parcelize.Parcelize
import java.util.*
//для хранения в бд
@Parcelize
@Entity(tableName = DatabaseContract.ActivityContract.TABLE_NAME)
data class ActivityData(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.ID)
    val id : Long ,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.DISTANCE)
    val distance : Float,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.NAME)
    val name : String,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.TYPE)
    val type : String,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.ELEVATION)
    val elevation: Float? = 0f,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.TIME)
    val time : Int,

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.DESC)
    val desc : String? = "",

    @ColumnInfo(name = DatabaseContract.ActivityContract.Columns.START_DATE)
    val startDate: Date
) : Parcelable {

    fun mapToActivityUIEntity(username : String,  avatar : String) : ActivityUIEntity =
        ActivityUIEntity(
            userFullName = username,
            avatarLink = avatar,
            activity = this
        )

}