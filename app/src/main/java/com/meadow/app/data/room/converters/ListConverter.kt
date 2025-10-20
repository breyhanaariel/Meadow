package com.meadow.app.data.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * ListConverter.kt
 *
 * Room doesn’t support complex types (like List or Map) directly.
 * This converter serializes/deserializes them as JSON strings.
 */

object ListConverter {
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList<String>())
    }

    @TypeConverter
    @JvmStatic
    fun toList(json: String?): List<String> {
        return gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
    }
}
