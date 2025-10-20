package com.meadow.app.domain.model

/**
 * Script.kt
 *
 * Represents a script document (Markdown or Fountain).
 * Contains parsed text and metadata for editor features.
 */

data class Script(
    val id: String,
    val projectId: String,
    val title: String,
    val format: String,
    val content: String,
    val lastModified: Long,
    val versions: List<ScriptVersion> = emptyList()
)

data class ScriptVersion(
    val id: Int,
    val scriptId: String,
    val timestamp: Long,
    val content: String
)


