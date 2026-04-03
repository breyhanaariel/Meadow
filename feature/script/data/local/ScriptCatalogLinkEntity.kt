package com.meadow.feature.script.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "script_catalog_links",
    indices = [
        Index(value = ["scriptId"]),
        Index(value = ["variantId"]),
        Index(value = ["catalogId"]),
        Index(value = ["blockId"])
    ]
)
data class ScriptCatalogLinkEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    val scriptId: String,
    val variantId: String,
    val blockId: String,
    val rangeStartInBlock: Int,
    val rangeEndInBlock: Int,
    val catalogId: String,
    val aliasUsed: String
)
