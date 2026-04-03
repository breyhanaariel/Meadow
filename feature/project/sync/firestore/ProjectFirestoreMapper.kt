package com.meadow.feature.project.sync.firestore

import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.ProjectSyncMeta
import com.meadow.feature.project.domain.model.ProjectType

class ProjectFirestoreMapper {

    fun fromMap(
        id: String,
        data: Map<String, Any?>
    ): Project {
        val updatedAt = (data["updatedAt"] as? Number)?.toLong()
        val version = (data["version"] as? Number)?.toLong() ?: 0L

        return Project(
            id = id,
            seriesId = data["seriesId"] as? String,
            type = ProjectType.fromKey(data["type"] as? String),
            updatedAt = updatedAt,
            startDate = (data["startDate"] as? Number)?.toLong(),
            finishDate = (data["finishDate"] as? Number)?.toLong(),
            fields = emptyList(),
            syncMeta = ProjectSyncMeta(
                remoteVersion = version,
                remoteUpdatedAt = updatedAt
            )
        )
    }

    fun toMap(project: Project): Map<String, Any?> {
        val remoteVersion = project.syncMeta.remoteVersion
        val localVersion = project.syncMeta.localVersion
        val versionToWrite = maxOf(remoteVersion, localVersion)

        return mapOf(
            "seriesId" to project.seriesId,
            "type" to project.type.key,
            "updatedAt" to (project.updatedAt ?: project.syncMeta.localUpdatedAt),
            "startDate" to project.startDate,
            "finishDate" to project.finishDate,
            "version" to versionToWrite
        )
    }
}
