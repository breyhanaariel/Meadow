package com.meadow.app.utils.io

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * BackupUtils.kt
 *
 * Compresses project data into a ZIP file for offline backups.
 */
object BackupUtils {

    fun createBackupZip(context: Context, fileName: String, files: List<File>): File {
        val zipFile = File(context.filesDir, "$fileName.zip")
        ZipOutputStream(FileOutputStream(zipFile)).use { out ->
            files.forEach { file ->
                val entry = ZipEntry(file.name)
                out.putNextEntry(entry)
                file.inputStream().use { it.copyTo(out) }
                out.closeEntry()
            }
        }
        return zipFile
    }
}
