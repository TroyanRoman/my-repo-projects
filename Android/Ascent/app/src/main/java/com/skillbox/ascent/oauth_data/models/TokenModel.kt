package com.skillbox.ascent.oauth_data.models

data class TokenModel (
    val accessToken: String,
    val refreshToken: String,
    val expTime: Long,
)