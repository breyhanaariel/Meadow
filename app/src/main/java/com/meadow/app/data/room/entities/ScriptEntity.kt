package com.meadow.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single script document (Fountain or Markdown) inside a project.
 */
@Entity(tableName = "script_table")
data class ScriptEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val format: String,            // "fountain" or "markdown"
    val content: String,
    val lastModified: Long = System.currentTimeMillis()
)
