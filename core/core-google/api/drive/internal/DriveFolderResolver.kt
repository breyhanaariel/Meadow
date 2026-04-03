package com.meadow.core.google.api.drive.internal

import com.meadow.core.google.api.drive.DriveApi
import com.meadow.core.google.api.drive.model.DriveCreateFolderRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriveFolderResolver @Inject constructor(
    private val driveApi: DriveApi
) {

    suspend fun getOrCreateFolder(
        name: String,
        parentId: String? = null
    ): String {
        val query = buildString {
            append("name='$name' and mimeType='application/vnd.google-apps.folder'")
            if (parentId != null) {
                append(" and '$parentId' in parents")
            }
            append(" and trashed=false")
        }

        val existing = driveApi.listFiles(query)
            .body()
            ?.files
            ?.firstOrNull()

        if (existing != null) return existing.id

        val body = DriveCreateFolderRequest(
            name = name,
            mimeType = "application/vnd.google-apps.folder",
            parents = parentId?.let { listOf(it) }
        )

        return driveApi.createFolder(body)
            .body()
            ?.id
            ?: error("Failed to create folder $name")
    }
}
