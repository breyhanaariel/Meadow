package com.meadow.app.utils

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * ExportUtils.kt
 *
 * Handles exporting project data or assets (e.g., catalog or scripts)
 * to local storage or a selectable folder (for backups or JSON export).
 */

object ExportUtils {

    /**
     * Save text content (JSON, Fountain, Markdown, etc.) to a file.
     */
    fun saveTextToFile(context: Context, uri: Uri, content: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(content.toByteArray())
                stream.flush()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Creates or retrieves a file inside a DocumentFile directory.
     */
    fun getOrCreateFile(dir: DocumentFile, name: String, mime: String = "application/json"): DocumentFile? {
        return dir.findFile(name) ?: dir.createFile(mime, name)
    }

    /**
     * Save a text file to internal app storage.
     */
    fun saveToInternalStorage(context: Context, filename: String, content: String): File {
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { it.write(content.toByteArray()) }
        return file
    }
}
