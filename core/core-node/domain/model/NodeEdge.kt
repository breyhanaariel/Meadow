package com.meadow.core.node.domain.model

data class NodeEdge(
    val edgeId: String,
    val fromNodeId: String,
    val toNodeId: String,
    val type: String,
    val metadataJson: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean,
    val syncMeta: NodeEdgeSyncMeta = NodeEdgeSyncMeta()
)

data class NodeEdgeSyncMeta(
    val localVersion: Long = 0L,
    val remoteVersion: Long = 0L,
    val isDirty: Boolean = false,
    val lastSyncAt: Long? = null
)
