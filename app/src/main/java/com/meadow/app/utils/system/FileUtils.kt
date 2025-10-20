package com.meadow.app.utils.file

import android.content.Context
import java.io.File
import java.io.FileOutputStream

/**
 * FileUtils.kt
 *
 * Helper for saving JSON exports and temporary project backups.
 */
object FileUtils {

    fun saveJson(context: Context, fileName: String, jsonContent: String): File {
        val dir = File(context.filesDir, "exports")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "$fileName.json")
        FileOutputStream(file).use { it.write(jsonContent.toByteArray()) }
        return file
    }

    fun createTempFile(context: Context, prefix: String, extension: String = ".tmp"): File {
        val tempDir = File(context.cacheDir, "temp")
        if (!tempDir.exists()) tempDir.mkdirs()
        return File.createTempFile(prefix, extension, tempDir)
    }

    fun deleteFile(file: File) {
        if (file.exists()) file.delete()
    }
}
