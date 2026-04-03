package com.meadow.core.google.engine

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthManager @Inject constructor(
    private val oauthService: GoogleOAuthTokenService
) {

    private var accessToken: String? = null
    private var refreshToken: String? = null

    fun accessToken(): String? = accessToken

    suspend fun exchangeAuthCode(
        authCode: String,
        clientId: String,
        clientSecret: String
    ) {
        val response =
            oauthService.exchangeAuthCode(
                authCode = authCode,
                clientId = clientId,
                clientSecret = clientSecret
            )

        accessToken = response.accessToken
        refreshToken = response.refreshToken ?: refreshToken
    }

    fun isSignedIn(): Boolean =
        accessToken() != null


    suspend fun refreshAccessToken(
        clientId: String,
        clientSecret: String
    ) {
        val refresh = refreshToken ?: return

        val response =
            oauthService.refreshAccessToken(
                refreshToken = refresh,
                clientId = clientId,
                clientSecret = clientSecret
            )

        accessToken = response.accessToken
    }

    fun clear() {
        accessToken = null
        refreshToken = null
    }
}
