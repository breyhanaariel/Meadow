package com.meadow.core.export

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object AudioExporter {
    suspend fun convertToOgg(input: File, outFile: File) = withContext(Dispatchers.IO) {
        input.copyTo(outFile, overwrite = true)
    }
}