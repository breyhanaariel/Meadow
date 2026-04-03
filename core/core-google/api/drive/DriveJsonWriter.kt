package com.meadow.core.google.api.drive

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Writes JSON files to Google Drive using the REST API.
 * Android-native, OAuth-token based, SDK-free.
 */
class DriveJsonWriter(
    private val httpClient: OkHttpClient,
    private val accessTokenProvider: () -> String
) {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Creates a JSON file in the given Drive folder.
     * Returns the Drive file ID.
     */
    fun writeJson(
        parentFolderId: String,
        fileName: String,
        jsonContent: String
    ): String {

        val metadata = buildJsonMetadata(
            parentFolderId = parentFolderId,
            fileName = fileName
        )

        val boundary = "meadow-boundary-${System.currentTimeMillis()}"

        val multipartBody = buildMultipartBody(
            boundary = boundary,
            metadata = metadata,
            jsonContent = jsonContent
        )

        val request = Request.Builder()
            .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
            .addHeader("Authorization", "Bearer ${accessTokenProvider()}")
            .addHeader(
                "Content-Type",
                "multipart/related; boundary=$boundary"
            )
            .post(multipartBody)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("Drive JSON upload failed: ${response.code}")
            }

            val body = response.body?.string()
                ?: error("Empty Drive response")

            val jsonObj = json.parseToJsonElement(body).jsonObject
            return jsonObj["id"]!!.jsonPrimitive.content
        }
    }

    private fun buildJsonMetadata(
        parentFolderId: String,
        fileName: String
    ): String {
        return """
            {
              "name": "$fileName",
              "parents": ["$parentFolderId"],
              "mimeType": "application/json"
            }
        """.trimIndent()
    }

    private fun buildMultipartBody(
        boundary: String,
        metadata: String,
        jsonContent: String
    ) = """
        --$boundary
        Content-Type: application/json; charset=UTF-8

        $metadata
        --$boundary
        Content-Type: application/json; charset=UTF-8

        $jsonContent
        --$boundary--
    """.trimIndent()
        .toRequestBody("multipart/related; boundary=$boundary".toMediaType())
}
