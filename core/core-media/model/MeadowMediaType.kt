package com.meadow.core.media.model

enum class MeadowMediaType(
    val mimeType: String,
    val extensions: Set<String>
) {

    IMAGE(
        mimeType = "image/*",
        extensions = setOf("png", "webp")
    ),

    VIDEO(
        mimeType = "video/*",
        extensions = setOf("ogv", "webm")
    ),

    AUDIO(
        mimeType = "audio/*",
        extensions = setOf("ogg", "opus")
    ),

    SUBTITLE(
        mimeType = "text/*",
        extensions = setOf("srt", "vtt", "ass")
    ),

    DOCUMENT(
        mimeType = "*/*",
        extensions = setOf("pdf", "epub")
    ),

    FILE(
        mimeType = "*/*",
        extensions = emptySet()
    );

    companion object {

        /* Finds media type from file extension */

        fun fromExtension(extension: String): MeadowMediaType? {

            val ext = extension.lowercase()

            return entries.firstOrNull {
                ext in it.extensions
            }
        }
    }
}