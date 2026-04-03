package com.meadow.core.media.loader

import android.content.Context
import java.io.File

object MediaCache {

    fun getCacheDir(context: Context): File {
        val dir = File(context.cacheDir, "media_cache")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun clearCache(context: Context) {
        getCacheDir(context).deleteRecursively()
    }
}