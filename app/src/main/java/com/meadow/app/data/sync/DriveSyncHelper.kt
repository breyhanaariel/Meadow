package com.meadow.app.sync

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DriveSyncHelper(private val ctx: Context) {
    private val TAG = "DriveSyncHelper"
    private var driveService: Drive? = null

    suspend fun signIn(activity: Activity): Boolean {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("795264131313-08uoko1dqchj91ao0gbmvu7j3v9sae0l.apps.googleusercontent.com")
            .requestScopes(
                Scope("https://www.googleapis.com/auth/drive.file"),
                Scope("https://www.googleapis.com/auth/calendar")
            )
            .build()
        val client = GoogleSignIn.getClient(activity, options)
        val account = GoogleSignIn.getLastSignedInAccount(ctx)
        if (account != null && GoogleSignIn.hasPermissions(account, Scope("https://www.googleapis.com/auth/drive.file"))) {
            createDriveService(account)
            return true
        }
        activity.startActivityForResult(client.signInIntent, SIGN_IN_REQUEST_CODE)
        return false
    }

    fun handleSignInResult(intentData: android.content.Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intentData)
        if (task.isSuccessful) {
            createDriveService(task.result)
        } else {
            Log.e(TAG, "Sign-in failed: ${task.exception}")
        }
    }

    private fun createDriveService(account: GoogleSignInAccount) {
        val credential = GoogleAccountCredential.usingOAuth2(ctx, listOf("https://www.googleapis.com/auth/drive.file"))
        credential.selectedAccount = account.account
        driveService = Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
            .setApplicationName("Meadow")
            .build()
    }

    suspend fun uploadFile(name: String, mime: String, contentUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val metadata = File().apply { this.name = name }
            val stream = ctx.contentResolver.openInputStream(contentUri) ?: return@withContext false
            val media = com.google.api.client.http.InputStreamContent(mime, stream)
            driveService?.files()?.create(metadata, media)?.setFields("id")?.execute()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Drive upload failed", e)
            false
        }
    }

    suspend fun listFiles(): List<File> = withContext(Dispatchers.IO) {
        val out = mutableListOf<File>()
        try {
            var pageToken: String? = null
            do {
                val list = driveService!!.files().list().setSpaces("drive").setFields("nextPageToken, files(id, name)").setPageToken(pageToken).execute()
                out.addAll(list.files)
                pageToken = list.nextPageToken
            } while (pageToken != null)
        } catch (e: Exception) { Log.e(TAG, "List failed", e) }
        out
    }

    companion object { const val SIGN_IN_REQUEST_CODE = 9001 }
}
