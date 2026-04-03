package com.meadow.core.google.api.drive.internal

import com.google.gson.Gson
import javax.inject.Inject
import com.meadow.core.google.api.drive.model.DriveBackupMeta

class DriveBackupSerializer @Inject constructor(
    private val gson: Gson,
    private val providers: List<@JvmSuppressWildcards BackupDataSource>
) {

    suspend fun generateFullBackup(
        projectId: String? = null
    ): String {

        val payload = providers.associate { it.key() to it.exportData() }

        val wrapper = mapOf(
            "meta" to DriveBackupMeta(
                projectId = projectId,
                timestamp = System.currentTimeMillis(),
                appVersion = null
            ),
            "data" to payload
        )

        return gson.toJson(wrapper)
    }

    suspend fun restoreFromJson(json: String) {
        val root = gson.fromJson(json, Map::class.java) as Map<String, Any>
        val data = root["data"] as? Map<String, Any> ?: return

        providers.forEach { provider ->
            data[provider.key()]?.let { provider.importData(it) }
        }
    }
}
