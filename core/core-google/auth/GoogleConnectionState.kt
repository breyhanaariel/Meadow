package com.meadow.core.google.auth

sealed class GoogleConnectionState {

    data object Disconnected : GoogleConnectionState()

    data object Connecting : GoogleConnectionState()

    data class Connected(
        val email: String,
        val grantedScopes: Set<String>
    ) : GoogleConnectionState()

    data class Error(
        val throwable: Throwable
    ) : GoogleConnectionState()
}
