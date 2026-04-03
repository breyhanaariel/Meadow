package com.meadow.core.export

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageExporter {

    suspend fun exportPng(
        input: File,
        outFile: File
    ) = withContext(Dispatchers.IO) {

        val bitmap =
            BitmapFactory.decodeFile(input.absolutePath)
                ?: error("Unable to decode image")

        FileOutputStream(outFile).use { out ->
            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                out
            )
        }
    }

    suspend fun exportWebp(
        input: File,
        outFile: File,
        quality: Int = 90
    ) = withContext(Dispatchers.IO) {

        val bitmap =
            BitmapFactory.decodeFile(input.absolutePath)
                ?: error("Unable to decode image")

        FileOutputStream(outFile).use { out ->
            bitmap.compress(
                Bitmap.CompressFormat.WEBP,
                quality,
                out
            )
        }
    }
}