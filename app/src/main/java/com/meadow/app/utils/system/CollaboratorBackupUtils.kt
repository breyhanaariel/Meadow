package com.meadow.app.utils.system

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * CollaboratorBackupUtils.kt
 *
 * Exports and imports collaborators for offline backup as JSON.
 */

object CollaboratorBackupUtils {

    @kotlinx.serialization.Serializable
    data class CollaboratorExport(
        val name: String,
        val email: String,
        val role: String
    )

    suspend fun exportCollaborators(context: Context, collaborators: List<CollaboratorExport>) {
        withContext(Dispatchers.IO) {
            val file = File(context.getExternalFilesDir(null), "collaborators_backup.json")
            val json = Json.encodeToString(collaborators)
            file.writeText(json)
        }
    }

    suspend fun importCollaborators(context: Context): List<CollaboratorExport> {
        return withContext(Dispatchers.IO) {
            val file = File(context.getExternalFilesDir(null), "collaborators_backup.json")
            if (!file.exists()) return@withContext emptyList()
            val json = file.readText()
            Json.decodeFromString(json)
        }
    }
}