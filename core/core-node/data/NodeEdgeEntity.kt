package com.meadow.core.node.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "node_edges",
    indices = [
        Index(value = ["fromNodeId"]),
        Index(value = ["toNodeId"]),
        Index(value = ["type"]),
        Index(value = ["isDeleted"])
    ]
)
data class NodeEdgeEntity(
    @PrimaryKey val edgeId: String,
    val fromNodeId: String,
    val toNodeId: String,
    val type: String,
    val metadataJson: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean,
    val localVersion: Long,
    val remoteVersion: Long,
    val isDirty: Boolean,
    val lastSyncAt: Long?
)
