package com.meadow.core.google.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class GoogleAuthManager(
    private val httpClient: OkHttpClient = OkHttpClient()
) {

    private val _connectionState =
        MutableStateFlow<GoogleConnectionState>(
            GoogleConnectionState.Disconnected
        )

    val connectionState: StateFlow<GoogleConnectionState> =
        _connectionState

    private var accessToken: String? = null
    private var refreshToken: String? = null

    fun getAccessToken(): String? = accessToken

    fun setConnecting() {
        _connectionState.value = GoogleConnectionState.Connecting
    }

    fun clear() {
        accessToken = null
        refreshToken = null
        _connectionState.value = GoogleConnectionState.Disconnected
    }

    /**
     * FINAL STEP: exchange server auth code for OAuth tokens
     */
    suspend fun exchangeAuthCode(
        authCode: String,
        clientId: String,
        clientSecret: String
    ) {
        try {
            val body = FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", authCode)
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("redirect_uri", "postmessage")
                .build()

            val request = Request.Builder()
                .url("https://oauth2.googleapis.com/token")
                .post(body)
                .build()

            val response = withContext(Dispatchers.IO) {
                httpClient.newCall(request).execute()
            }

            response.use {
                val responseBody = it.body?.string()

                if (!it.isSuccessful) {
                    throw IllegalStateException(
                        "Token exchange failed: ${it.code} $responseBody"
                    )
                }

                val json = JSONObject(responseBody ?: "{}")

                accessToken = json.getString("access_token")
                refreshToken = json.optString("refresh_token", null)

                _connectionState.value =
                    GoogleConnectionState.Connected(
                        email = "Google Account",
                        grantedScopes = emptySet()
                    )
            }

        } catch (t: Throwable) {
            _connectionState.value =
                GoogleConnectionState.Error(t)
        }
    }
}
