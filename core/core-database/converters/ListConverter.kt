package com.meadow.core.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ListConverter {
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromList(list: List<String>?): String =
        gson.toJson(list ?: emptyList<String>())

    @TypeConverter
    @JvmStatic
    fun toList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
}
