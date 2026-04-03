package com.meadow.core.utils.files

import android.content.Context
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.IOException

object JsonUtils {

    val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    inline fun <reified T> readJson(context: Context, path: String): T? {
        return try {
            val file = context.filesDir.resolve(path)
            val text: String = if (file.exists()) {
                file.readText()
            } else {
                val input = context.assets.open(path)
                input.bufferedReader().use { it.readText() }
            }

            json.decodeFromString<T>(text)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun saveJson(context: Context, fileName: String, data: Any) {
        try {
            val file = context.filesDir.resolve(fileName)
            file.writeText(json.encodeToString(data))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
