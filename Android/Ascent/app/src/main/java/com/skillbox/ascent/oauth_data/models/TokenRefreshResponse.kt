package com.skillbox.ascent.oauth_data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class TokenRefreshResponse(
    @Json(name = "access_token")
    val accessToken : String,
    @Json(name = "expires_at")
    val expiresAt : Int,
    @Json(name = "expires_in")
    val expiresIn : Int,
    @Json(name = "refresh_token")
    val refreshToken : String
) : Parcelable {
    fun toTokenModel() : TokenModel{
        return TokenModel(
            accessToken = this.accessToken,
            expTime = this.expiresAt.toLong(),
            refreshToken = this.refreshToken,
        )
    }
}