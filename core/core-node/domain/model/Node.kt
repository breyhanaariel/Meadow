package com.meadow.core.node.domain.model

data class Node(
    val nodeId: String,
    val scope: NodeScope,
    val parentNodeId: String?,
    val type: String,
    val title: String,
    val content: String?,
    val orderKey: Long,
    val metadataJson: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean,
    val syncMeta: NodeSyncMeta = NodeSyncMeta()
)

data class NodeSyncMeta(
    val localVersion: Long = 0L,
    val remoteVersion: Long = 0L,
    val isDirty: Boolean = false,
    val lastSyncAt: Long? = null
)
