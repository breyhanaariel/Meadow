package com.meadow.core.auth.api

import com.meadow.core.auth.domain.AuthState
import com.meadow.core.common.functional.ResultX
import kotlinx.coroutines.flow.StateFlow

interface AuthManager {

    val authState: StateFlow<AuthState>

    suspend fun signInAnonymously(): ResultX<Unit>

    suspend fun signInWithGoogle(): ResultX<Unit>

    suspend fun signOut(): ResultX<Unit>

    suspend fun revokeGoogleAccess(): ResultX<Unit>

    suspend fun refreshUser(): ResultX<Unit>
}
