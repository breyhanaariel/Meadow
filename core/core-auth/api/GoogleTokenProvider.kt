package com.meadow.core.auth.api

import android.content.Context

interface GoogleTokenProvider {
    fun idToken(context: Context): String?
    suspend fun signOut(context: Context)
    suspend fun revokeAccess(context: Context)
}
