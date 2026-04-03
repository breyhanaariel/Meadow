package com.meadow.core.ai.pdf.reference

import androidx.room.TypeConverter

class ReferenceEmbeddingConverters {

    @TypeConverter
    fun fromList(list: List<Float>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String?): List<Float>? {
        return data?.split(",")?.map { it.toFloat() }
    }
}
