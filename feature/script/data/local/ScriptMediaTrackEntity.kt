package com.meadow.feature.script.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "script_media_tracks",
    indices = [
        Index(value = ["scriptId"]),
        Index(value = ["kind"]),
        Index(value = ["language"])
    ]
)
data class ScriptMediaTrackEntity(
    @PrimaryKey
    val trackId: String = UUID.randomUUID().toString(),

    val scriptId: String,
    val kind: String,
    val assetId: String,
    val language: String?,
    val label: String?,
    val isDefault: Int,
    val isForced: Int,
    val styleAssetId: String?
)
