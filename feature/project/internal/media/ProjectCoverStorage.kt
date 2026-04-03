package com.meadow.feature.project.internal.media

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

class ProjectCoverStorage(
    private val context: Context
) {

    fun copyCoverIntoAppStorage(sourceUri: Uri): String {
        val dir = File(context.filesDir, "project_covers")
        if (!dir.exists()) dir.mkdirs()

        val ext = guessExtension(context.contentResolver, sourceUri) ?: "jpg"
        val outFile = File(dir, "cover_${UUID.randomUUID()}.$ext")

        context.contentResolver.openInputStream(sourceUri).use { input ->
            requireNotNull(input) { "Unable to open input stream for $sourceUri" }
            outFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return outFile.absolutePath
    }

    private fun guessExtension(resolver: ContentResolver, uri: Uri): String? {
        val type = resolver.getType(uri) ?: return null
        return when (type.lowercase()) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/jpeg", "image/jpg" -> "jpg"
            else -> null
        }
    }
}