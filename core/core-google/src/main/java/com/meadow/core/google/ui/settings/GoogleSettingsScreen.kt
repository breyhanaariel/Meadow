package com.meadow.core.google.ui.settings

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.Scope
import com.meadow.core.google.BuildConfig
import com.meadow.core.google.R
import com.meadow.core.google.auth.GoogleScopes
import com.meadow.core.google.auth.GoogleSignInManager
import com.meadow.core.ui.components.MeadowButton

@Composable
fun GoogleSettingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as Activity

    var signedInAccount by remember {
        mutableStateOf(GoogleSignIn.getLastSignedInAccount(context))
    }

    val needs = remember {
        GoogleScopes.ALL_SCOPES
            .map { Scope(it) }
            .toTypedArray()
    }

    val signInClient = remember {
        GoogleSignInManager(context)
            .buildSignInClient(
                scopes = GoogleScopes.ALL_SCOPES,
                serverClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
            )
    }

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account =
                    task.getResult(
                        com.google.android.gms.common.api.ApiException::class.java
                    )

                Log.e("GOOGLE_AUTH", "Signed in: ${account.email}")

                signedInAccount = account

                val hasAll = GoogleSignIn.hasPermissions(account, *needs)

                if (!hasAll) {
                    Log.e("GOOGLE_AUTH", "Requesting additional scopes...")
                    GoogleSignIn.requestPermissions(
                        activity,
                        1001,
                        account,
                        *needs
                    )
                } else {
                    account.grantedScopes.forEach {
                        Log.e("GOOGLE_AUTH", "GRANTED: ${it.scopeUri}")
                    }
                }

            } catch (e: Exception) {
                Log.e("GOOGLE_AUTH", "Sign-in failed", e)
                signedInAccount = null
            }
        }

    LaunchedEffect(signedInAccount) {
        val account = signedInAccount
        if (account != null) {
            val hasAll = GoogleSignIn.hasPermissions(account, *needs)
            if (!hasAll) {
                Log.e("GOOGLE_AUTH", "Missing scopes on launch, requesting...")
                GoogleSignIn.requestPermissions(
                    activity,
                    1001,
                    account,
                    *needs
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        if (signedInAccount == null) {

            MeadowButton(
                text = stringResource(R.string.google_connect_account),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    launcher.launch(signInClient.signInIntent)
                }
            )

        } else {

            Text(
                text = "Signed in as: ${signedInAccount?.email}"
            )

            MeadowButton(
                text = stringResource(R.string.google_account_disconnected),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    signInClient.signOut().addOnCompleteListener {
                        signedInAccount = null
                    }
                }
            )
        }
    }
}
