package com.meadow.core.google.api.drive.internal

import javax.inject.Inject

class DriveBackupRepository @Inject constructor(
    private val api: DriveApiClient,
    private val serializer: DriveBackupSerializer
) {

    suspend fun backupFile(
        fileName: String,
        json: String,
        appProperties: Map<String, String>? = null
    ): DriveUploadResult {
        return try {
            api.upsertFile(
                fileName = fileName,
                json = json,
                appProperties = appProperties
            )
            DriveUploadResult.Success
        } catch (e: Exception) {
            DriveUploadResult.Failure("Backup failed", e)
        }
    }

    suspend fun restoreLatest(): String? {
        return try {
            api.downloadLatestBackup()
        } catch (e: Exception) {
            null
        }
    }
}
