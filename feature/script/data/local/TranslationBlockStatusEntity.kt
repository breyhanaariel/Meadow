package com.meadow.feature.script.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "script_translation_block_status",
    primaryKeys = ["targetVariantId", "blockId"],
    indices = [
        Index(value = ["scriptId"]),
        Index(value = ["canonicalVariantId"])
    ]
)
data class TranslationBlockStatusEntity(
    val scriptId: String,
    val canonicalVariantId: String,
    val targetVariantId: String,
    val blockId: String,
    val status: String,
    val canonicalFingerprintAtTranslation: String,
    val updatedAt: Long
)
