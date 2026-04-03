package com.meadow.core.google.engine

data class GoogleTokenResponse(
    val accessToken: String,
    val expiresIn: Long,
    val refreshToken: String? = null
)
