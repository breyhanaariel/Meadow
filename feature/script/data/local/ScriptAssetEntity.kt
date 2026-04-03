package com.meadow.feature.script.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "script_assets",
    indices = [
        Index(value = ["scriptId"]),
        Index(value = ["variantId"]),
        Index(value = ["type"])
    ]
)
data class ScriptAssetEntity(
    @PrimaryKey
    val assetId: String = UUID.randomUUID().toString(),

    val scriptId: String,
    val variantId: String?,
    val type: String,
    val uri: String,
    val mimeType: String?,
    val metadataJson: String?,
    val createdAt: Long
)
