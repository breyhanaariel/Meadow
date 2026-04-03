package com.meadow.core.node.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "nodes",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["seriesId"]),
        Index(value = ["parentNodeId"]),
        Index(value = ["type"]),
        Index(value = ["isDeleted"])
    ]
)
data class NodeEntity(
    @PrimaryKey val nodeId: String,
    val projectId: String?,
    val seriesId: String?,
    val parentNodeId: String?,
    val type: String,
    val title: String,
    val content: String?,
    val orderKey: Long,
    val metadataJson: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean,
    val localVersion: Long,
    val remoteVersion: Long,
    val isDirty: Boolean,
    val lastSyncAt: Long?
)
