package com.skillbox.ascent.data.ascent.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class AscentUser(
    val id: Long,
    @Json(name = "username")
    val userName : String? = "",
    @Json(name = "firstname")
    val firstName : String,
    @Json(name = "lastname")
    val lastName : String,
    @Json(name = "sex")
    val gender : String,
    val country : String? ,
    val weight : Float? ,
    @Json(name = "follower_count")
    val followersCount : Short,
    @Json(name = "friend_count")
    val friendsCount : Short,
    @Json(name = "profile_medium")
    val avatar: String
) : Parcelable
