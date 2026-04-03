package com.meadow.core.auth.util

import com.google.firebase.auth.FirebaseUser
import com.meadow.core.auth.domain.AuthUser

fun FirebaseUser.toAuthUser(): AuthUser {
    return AuthUser(
        uid = uid,
        isAnonymous = isAnonymous,
        displayName = displayName,
        email = email,
        photoUrl = photoUrl?.toString()
    )
}
