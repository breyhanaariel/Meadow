package com.meadow.core.google.engine

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class GoogleSignInManager @Inject constructor(
    private val signInClient: GoogleSignInClient,
    private val googleAuthManager: GoogleAuthManager
) {

    private var idToken: String? = null

    fun idToken(): String? = idToken

    fun signInIntent(): Intent = signInClient.signInIntent

    suspend fun handleSignInResult(
        data: Intent?,
        clientId: String,
        clientSecret: String
    ): Result<Unit> = runCatching {

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java)

        idToken = account.idToken

        val authCode = account.serverAuthCode
            ?: error("Missing serverAuthCode. Did you call requestServerAuthCode()?")

        googleAuthManager.exchangeAuthCode(
            authCode = authCode,
            clientId = clientId,
            clientSecret = clientSecret
        )
    }

    fun isSignedIn(): Boolean = idToken != null || googleAuthManager.isSignedIn()

    suspend fun signOut(context: Context) {
        signInClient.signOut().await()
        idToken = null
        googleAuthManager.clear()
    }

    suspend fun revokeAccess(context: Context) {
        signInClient.revokeAccess().await()
        idToken = null
        googleAuthManager.clear()
    }
}
