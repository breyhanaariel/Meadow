package com.meadow.core.google.api.drive.internal

interface DriveApiClient {

    suspend fun upsertFile(
        fileName: String,
        json: String,
        appProperties: Map<String, String>? = null
    )


    suspend fun downloadLatestBackup(): String?

    suspend fun getOrCreateFolder(
        name: String
    ): String

    suspend fun getOrCreateSpreadsheet(
        name: String,
        parentFolderId: String
    ): String
}
