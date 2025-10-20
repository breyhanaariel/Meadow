package com.meadow.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mindmap_node_table")
data class MindMapNodeEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val label: String,
    val x: Float = 0f,
    val y: Float = 0f,
    val linksJson: String? = null // connections to other nodes
)
