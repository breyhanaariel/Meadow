package com.meadow.core.media.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import java.io.InputStream

object MediaUtils {

    fun getDuration(file: File): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.path)
        val duration =
            retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )
        retriever.release()
        return duration?.toLongOrNull() ?: 0L
    }

    fun getResolution(file: File): Pair<Int, Int> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.path)
        val width =
            retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
            )?.toIntOrNull() ?: 0
        val height =
            retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
            )?.toIntOrNull() ?: 0
        retriever.release()
        return width to height
    }

    fun copyToAppStorage(
        context: Context,
        uri: Uri,
        folder: String,
        extension: String? = null
    ): String {
        val inputStream: InputStream =
            context.contentResolver.openInputStream(uri)
                ?: error("Unable to open media input stream")

        val dir = File(context.filesDir, folder)
        if (!dir.exists()) dir.mkdirs()

        val fileName =
            buildString {
                append(System.currentTimeMillis())
                extension?.let { append(".$it") }
            }

        val file = File(dir, fileName)

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        return file.absolutePath
    }
}