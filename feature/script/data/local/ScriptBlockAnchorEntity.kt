package com.meadow.feature.script.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "script_block_anchors",
    primaryKeys = ["variantId", "blockId"],
    indices = [
        Index(value = ["scriptId"]),
        Index(value = ["variantId"]),
        Index(value = ["orderKey"])
    ]
)
data class ScriptBlockAnchorEntity(
    val scriptId: String,
    val variantId: String,
    val blockId: String,
    val kind: String,
    val fingerprint: String,
    val orderKey: Long,
    val createdAt: Long
)
