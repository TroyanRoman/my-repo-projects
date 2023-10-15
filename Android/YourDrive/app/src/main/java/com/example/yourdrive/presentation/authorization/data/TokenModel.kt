package com.example.yourdrive.presentation.authorization.data

data class TokenModel(
    val accessToken: String,
    val refreshToken: String,
    val expirationTimeInMillis: Long,
)