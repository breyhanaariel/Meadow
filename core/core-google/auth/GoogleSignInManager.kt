package com.meadow.core.google.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class GoogleSignInManager(
    private val context: Context
) {

    fun buildSignInClient(
        scopes: List<String>,
        serverClientId: String
    ) = GoogleSignIn.getClient(
        context,
        GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestEmail()
            .requestServerAuthCode(serverClientId, true)
            .apply {
                scopes.forEach { scope ->
                    requestScopes(
                        com.google.android.gms.common.api.Scope(scope)
                    )
                }
            }
            .build()
    )
}
