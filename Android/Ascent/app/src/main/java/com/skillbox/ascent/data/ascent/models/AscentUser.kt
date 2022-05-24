package com.skillbox.ascent.data.ascent.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skillbox.ascent.data.ascent.db.DatabaseContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
//@Entity(
//    tableName = DatabaseContract.UserContract.TABLE_NAME
//)
data class AscentUser(
    @PrimaryKey(autoGenerate = true)
    //@ColumnInfo(name = DatabaseContract.UserContract.Columns.ID)
    val id: Long,

    @Json(name = "username")
    val userName : String? = "",
    @Json(name = "firstname")
   // @ColumnInfo(name = DatabaseContract.UserContract.Columns.FIRST_NAME)
    val firstName : String,
    @Json(name = "lastname")
   // @ColumnInfo(name = DatabaseContract.UserContract.Columns.LAST_NAME)
    val lastName : String,
    @Json(name = "sex")
   // @ColumnInfo(name = DatabaseContract.UserContract.Columns.GENDER)
    val gender : String,
   // @ColumnInfo(name = DatabaseContract.UserContract.Columns.COUNTRY)
    val country : String?,
  //  @ColumnInfo(name = DatabaseContract.UserContract.Columns.WEIGHT)
    val weight : Float? ,
    @Json(name = "follower_count")
   // @ColumnInfo(name = DatabaseContract.UserContract.Columns.FOLLOWERS_COUNT)
    val followersCount : Short,
    @Json(name = "friend_count")
   // @ColumnInfo(name = DatabaseContract.UserContract.Columns.FRIENDS_COUNT)
    val friendsCount : Short,
    @Json(name = "profile_medium")
   // @ColumnInfo(name = DatabaseContract.UserContract.Columns.AVATAR)
    val avatar: String

) : Parcelable
