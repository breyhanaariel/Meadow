package com.meadow.core.utils.files

import java.io.File

object FileUtils {
    fun deleteIfExists(file: File) {
        if (file.exists()) file.delete()
    }

    fun createDirIfMissing(dir: File) {
        if (!dir.exists()) dir.mkdirs()
    }
}