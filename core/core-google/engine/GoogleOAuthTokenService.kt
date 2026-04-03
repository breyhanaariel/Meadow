package com.meadow.core.google.engine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleOAuthTokenService @Inject constructor(
    private val client: OkHttpClient
) {

    suspend fun refreshAccessToken(
        refreshToken: String,
        clientId: String,
        clientSecret: String
    ): GoogleTokenResponse {

        val body = FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", refreshToken)
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .build()

        val request = Request.Builder()
            .url("https://oauth2.googleapis.com/token")
            .post(body)
            .build()

        val responseString = withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { resp ->
                val bodyStr = resp.body?.string()
                if (!resp.isSuccessful) {
                    error("Refresh token failed: ${resp.code} $bodyStr")
                }
                bodyStr ?: error("Empty refresh response body")
            }
        }

        val json = JSONObject(responseString)

        return GoogleTokenResponse(
            accessToken = json.getString("access_token"),
            expiresIn = json.getLong("expires_in"),
            refreshToken = refreshToken
        )
    }

    suspend fun exchangeAuthCode(
        authCode: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String = "postmessage"
     ): GoogleTokenResponse {

        val body = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", authCode)
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("redirect_uri", redirectUri)
            .build()

        val request = Request.Builder()
            .url("https://oauth2.googleapis.com/token")
            .post(body)
            .build()

        val responseString = withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { resp ->
                val bodyStr = resp.body?.string()
                if (!resp.isSuccessful) {
                    error("Token exchange failed: ${resp.code} $bodyStr")
                }
                bodyStr ?: error("Empty token exchange response body")
            }
        }

        val json = JSONObject(responseString)

        return GoogleTokenResponse(
            accessToken = json.getString("access_token"),
            expiresIn = json.getLong("expires_in"),
            refreshToken = json.optString("refresh_token", null) // ✅ capture refresh token
        )
    }
}
