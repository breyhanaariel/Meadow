package com.meadow.app.util

import android.content.Context
import com.google.gson.Gson
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun exportJson(context: Context, fileName: String, json: String): File {
    val f = File(context.cacheDir, fileName)
    f.writeText(json)
    return f
}

fun exportToZip(context: Context, files: Map<String, ByteArray>, outName: String): File {
    val out = File(context.cacheDir, outName)
    ZipOutputStream(out.outputStream()).use { zip ->
        files.forEach { (name, bytes) ->
            zip.putNextEntry(ZipEntry(name))
            zip.write(bytes)
            zip.closeEntry()
        }
    }
    return out
}
