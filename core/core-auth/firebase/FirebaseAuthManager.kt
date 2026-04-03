package com.meadow.core.auth.firebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.meadow.core.auth.api.AuthManager
import com.meadow.core.auth.api.GoogleTokenProvider
import com.meadow.core.auth.domain.AuthState
import com.meadow.core.auth.util.toAuthUser
import com.meadow.core.common.functional.ResultX
import com.meadow.core.common.functional.safeCall
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleTokenProvider: GoogleTokenProvider,
    @ApplicationContext private val appContext: Context
) : AuthManager {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState

    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        _authState.value = if (user != null) {
            AuthState.SignedIn(user.toAuthUser())
        } else {
            AuthState.SignedOut
        }
    }

    init {
        firebaseAuth.addAuthStateListener(authListener)
        updateFromCurrentUser()
    }

    override suspend fun signInAnonymously(): ResultX<Unit> = safeCall {
        firebaseAuth.signInAnonymously().await()
        updateFromCurrentUser()
    }.map { Unit }

    override suspend fun signInWithGoogle(): ResultX<Unit> = safeCall {
        val idToken = googleTokenProvider.idToken(appContext)
            ?: throw IllegalStateException(
                "Google ID token is null. Ensure GoogleAuthManager.client(context, webClientId) was initialized with a valid web client id."
            )

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
        updateFromCurrentUser()
    }.map { Unit }

    override suspend fun signOut(): ResultX<Unit> = safeCall {
        firebaseAuth.signOut()
        googleTokenProvider.signOut(appContext)
        _authState.value = AuthState.SignedOut
    }.map { Unit }

    override suspend fun revokeGoogleAccess(): ResultX<Unit> = safeCall {
        firebaseAuth.signOut()
        googleTokenProvider.revokeAccess(appContext)
        _authState.value = AuthState.SignedOut
    }.map { Unit }

    override suspend fun refreshUser(): ResultX<Unit> = safeCall {
        updateFromCurrentUser()
    }.map { Unit }

    private fun updateFromCurrentUser() {
        val user = firebaseAuth.currentUser
        _authState.value = if (user != null) {
            AuthState.SignedIn(user.toAuthUser())
        } else {
            AuthState.SignedOut
        }
    }
}
