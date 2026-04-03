package com.meadow.core.google.api.drive

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.serialization.json.*

class DriveFolderManager(
    private val httpClient: OkHttpClient,
    private val accessTokenProvider: () -> String
) {

    private val json = Json { ignoreUnknownKeys = true }

    fun getOrCreateFolder(
        name: String,
        parentId: String? = null
    ): String {

        val existing = findFolder(name, parentId)
        if (existing != null) return existing

        return createFolder(name, parentId)
    }

    private fun findFolder(
        name: String,
        parentId: String?
    ): String? {

        val query = buildString {
            append("mimeType='application/vnd.google-apps.folder'")
            append(" and name='$name'")
            if (parentId != null) {
                append(" and '$parentId' in parents")
            }
            append(" and trashed=false")
        }

        val url =
            "https://www.googleapis.com/drive/v3/files?q=${query.encode()}&spaces=drive"

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer ${accessTokenProvider()}")
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null

            val body = response.body?.string() ?: return null
            val root = json.parseToJsonElement(body).jsonObject
            val files = root["files"]?.jsonArray ?: return null

            return files.firstOrNull()
                ?.jsonObject
                ?.get("id")
                ?.jsonPrimitive
                ?.content
        }
    }

    private fun createFolder(
        name: String,
        parentId: String?
    ): String {

        val payload = buildJsonObject {
            put("name", name)
            put("mimeType", "application/vnd.google-apps.folder")
            if (parentId != null) {
                putJsonArray("parents") {
                    add(parentId)
                }
            }
        }

        val request = Request.Builder()
            .url("https://www.googleapis.com/drive/v3/files")
            .addHeader("Authorization", "Bearer ${accessTokenProvider()}")
            .addHeader("Content-Type", "application/json")
            .post(
                payload.toString()
                    .toRequestBody("application/json".toMediaType())
            )
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error("Failed to create Drive folder: ${response.code}")
            }

            val body = response.body?.string()!!
            val jsonObj = json.parseToJsonElement(body).jsonObject
            return jsonObj["id"]!!.jsonPrimitive.content
        }
    }
}

private fun String.encode(): String =
    java.net.URLEncoder.encode(this, Charsets.UTF_8.name())
