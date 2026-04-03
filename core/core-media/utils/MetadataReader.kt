package com.meadow.core.media.utils

import android.media.MediaMetadataRetriever
import java.io.File

object MetadataReader {
    fun readAll(file: File): Map<String, String> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.path)
        val map = mapOf(
            "Title" to (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: ""),
            "Artist" to (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: ""),
            "Album" to (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: ""),
            "Genre" to (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: ""),
            "Duration" to (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: ""),
        )
        retriever.release()
        return map
    }
}