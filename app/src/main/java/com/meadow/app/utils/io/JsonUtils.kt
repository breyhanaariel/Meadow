package com.meadow.app.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

/**
 * JsonExportUtils.kt
 *
 * Converts Meadow data models to and from JSON for backup or export.
 */

object JsonExportUtils {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun toJson(data: Any): String = gson.toJson(data)

    fun <T> fromJson(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)
}
