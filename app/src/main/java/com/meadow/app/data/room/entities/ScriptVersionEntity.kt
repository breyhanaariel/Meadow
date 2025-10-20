package com.meadow.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Autosave/version history snapshot for scripts.
 */
@Entity(tableName = "script_version_table")
data class ScriptVersionEntity(
    @PrimaryKey val id: String,
    val scriptId: String,
    val title: String? = null,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
