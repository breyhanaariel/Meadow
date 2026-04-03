package com.meadow.core.export

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object VideoExporter {
    suspend fun exportWebm(input: File, outFile: File) = withContext(Dispatchers.IO) {
        input.copyTo(outFile, overwrite = true)
    }
}