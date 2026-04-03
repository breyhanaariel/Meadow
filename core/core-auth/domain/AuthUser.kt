package com.meadow.core.auth.domain

data class AuthUser(
    val uid: String,
    val isAnonymous: Boolean,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)
