package com.meadow.core.google.api.drive.internal

import com.meadow.core.google.api.drive.DriveApi
import jakarta.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class DriveApiClientImpl @Inject constructor(
    private val driveApi: DriveApi,
    private val folderResolver: DriveFolderResolver
) : DriveApiClient {

    private var projectsFolderId: String? = null

    private suspend fun getProjectsFolder(): String {
        if (projectsFolderId == null) {
            val meadow = folderResolver.getOrCreateFolder("Meadow")
            projectsFolderId = folderResolver.getOrCreateFolder("Projects", meadow)
        }
        return projectsFolderId!!
    }

    override suspend fun getOrCreateFolder(
        name: String
    ): String =
        folderResolver.getOrCreateFolder(name)

    override suspend fun upsertFile(
        fileName: String,
        json: String,
        appProperties: Map<String, String>?
    ) {
        val folderId = getProjectsFolder()

        val query = buildString {
            append("trashed=false and '$folderId' in parents and ")
            if (appProperties != null && appProperties.isNotEmpty()) {
                append(DriveAppPropertiesQuery.build(appProperties))
            } else {
                append("name='$fileName'")
            }
        }

        val existing = driveApi.listFiles(query).body()
            ?.files
            ?.firstOrNull()

        val filePart = jsonPart(json)

        if (existing != null) {
            // Update the file contents
            driveApi.updateFile(
                fileId = existing.id,
                file = filePart
            )

            // Ensure appProperties exist on the file (safe even if already present)
            if (appProperties != null && appProperties.isNotEmpty()) {
                driveApi.patchFileMetadata(
                    fileId = existing.id,
                    body = mapOf("appProperties" to appProperties)
                )
            }
        } else {
            driveApi.createFile(
                metadata = metadataPart(fileName, folderId, appProperties),
                file = filePart
            )
        }
    }

    override suspend fun downloadLatestBackup(): String? {
        return null
    }

    override suspend fun getOrCreateSpreadsheet(
        name: String,
        parentFolderId: String
    ): String {

        val query =
            "name='$name' and " +
                    "mimeType='application/vnd.google-apps.spreadsheet' and " +
                    "'$parentFolderId' in parents and trashed=false"

        val existing = driveApi.listFiles(query).body()
            ?.files
            ?.firstOrNull()

        if (existing != null) return existing.id

        val metadataJson = """
        {
          "name": "$name",
          "mimeType": "application/vnd.google-apps.spreadsheet",
          "parents": ["$parentFolderId"]
        }
        """.trimIndent()

        val body =
            metadataJson.toRequestBody("application/json".toMediaType())

        val part = MultipartBody.Part.createFormData(
            name = "metadata",
            filename = "metadata.json",
            body = body
        )

        val response = driveApi.uploadFile(part).body()
            ?: error("Failed to create spreadsheet")

        return response.id
            ?: error("Drive upload succeeded but returned null id")
    }

    private fun jsonPart(json: String): MultipartBody.Part {
        val body = json.toRequestBody("application/json".toMediaType())
        return MultipartBody.Part.createFormData(
            name = "file",
            filename = "backup.json",
            body = body
        )
    }

    private fun metadataPart(
        fileName: String,
        folderId: String,
        appProperties: Map<String, String>?
    ): MultipartBody.Part {

        val appPropsJson = appProperties
            ?.entries
            ?.joinToString(separator = ",") { (k, v) -> """"$k":"$v"""" }
            ?.let { ""","appProperties":{ $it }""" }
            ?: ""

        val metadataJson = """
        {
          "name": "$fileName",
          "parents": ["$folderId"]$appPropsJson
        }
        """.trimIndent()

        val body = metadataJson.toRequestBody("application/json".toMediaType())
        return MultipartBody.Part.createFormData(
            name = "metadata",
            filename = "metadata.json",
            body = body
        )
    }
}
