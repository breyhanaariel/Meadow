package com.meadow.core.media.utils

import android.content.Context
import java.io.File

object MediaFileUtils {

    fun createMediaTempFile(context: Context, name: String, ext: String): File {
        val dir = File(context.cacheDir, "media_temp")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "$name.$ext")
    }

    fun deleteIfExists(file: File) {
        if (file.exists()) file.delete()
    }
}