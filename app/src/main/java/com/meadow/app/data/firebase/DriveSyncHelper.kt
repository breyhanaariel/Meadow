package com.meadow.app.data.firebase

import android.content.Context
import android.net.Uri
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream

/**
 * DriveSyncHelper.kt
 *
 * Handles Google Drive backup/export.
 * Allows manual user-initiated cloud backups.
 */

class DriveSyncHelper {

    private var driveService: Drive? = null

    /**
     * Initializes the Google Drive service using OAuth token.
     */
    suspend fun initialize(context: Context, token: String) = withContext(Dispatchers.IO) {
        val credential = GoogleCredential().setAccessToken(token)
        driveService = Drive.Builder(
            credential.transport,
            credential.jsonFactory,
            credential
        ).setApplicationName("Meadow").build()
    }

    /**
     * Uploads a file to the user’s Google Drive “Meadow Backups” folder.
     */
    suspend fun uploadBackup(localFilePath: String, projectName: String) = withContext(Dispatchers.IO) {
        val service = driveService ?: throw IllegalStateException("Drive not initialized")

        val folderMetadata = File().apply {
            name = "Meadow Backups"
            mimeType = "application/vnd.google-apps.folder"
        }

        // Ensure backup folder exists
        val folder = service.files().list()
            .setQ("name='Meadow Backups' and mimeType='application/vnd.google-apps.folder'")
            .execute().files.firstOrNull() ?: service.files().create(folderMetadata).execute()

        // Upload project backup file
        val fileMetadata = File().apply {
            name = "$projectName-backup.zip"
            parents = listOf(folder.id)
        }

        val fileContent = java.io.File(localFilePath)
        val fileStream = FileInputStream(fileContent)

        service.files().create(fileMetadata, com.google.api.client.http.InputStreamContent(null, fileStream))
            .execute()
    }

    /**
     * Optional: Download a backup from Drive.
     */
    suspend fun downloadBackup(fileId: String, destination: java.io.File) = withContext(Dispatchers.IO) {
        val service = driveService ?: throw IllegalStateException("Drive not initialized")
        val outputStream = destination.outputStream()
        service.files().get(fileId).executeMediaAndDownloadTo(outputStream)
        outputStream.close()
    }
}
