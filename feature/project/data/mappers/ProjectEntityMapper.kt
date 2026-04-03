package com.meadow.feature.project.data.mappers

import com.meadow.feature.project.data.local.ProjectEntity
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.ProjectType
import com.meadow.feature.project.domain.model.ProjectSyncMeta
import com.meadow.core.data.fields.FieldWithValue

object ProjectEntityMapper {

    fun toDomain(
        entity: ProjectEntity,
        fields: List<FieldWithValue> = emptyList()
    ): Project =
        Project(
            id = entity.id,
            seriesId = entity.seriesId,
            type = ProjectType.valueOf(entity.type),
            syncMeta = ProjectSyncMeta(
                localVersion = entity.localVersion,
                remoteVersion = entity.remoteVersion,
                localUpdatedAt = entity.localUpdatedAt ?: entity.updatedAt,
                remoteUpdatedAt = entity.remoteUpdatedAt,
                lastFirestoreSyncAt = entity.lastFirestoreSyncAt,
                lastDriveBackupAt = entity.lastDriveBackupAt,
                isDirty = entity.isDirty == 1,
                hasConflict = entity.hasConflict == 1,
                lastSyncError = entity.lastSyncError,
                retryCount = entity.retryCount
            ),
            updatedAt = entity.updatedAt,
            startDate = entity.startDate,
            finishDate = entity.finishDate,
            fields = fields
        )

    fun toEntity(domain: Project): ProjectEntity {
        val meta = domain.syncMeta

        return ProjectEntity(
            id = domain.id,
            seriesId = domain.seriesId,
            type = domain.type.name,
            updatedAt = domain.updatedAt,
            startDate = domain.startDate,
            finishDate = domain.finishDate,

            localVersion = meta.localVersion,
            remoteVersion = meta.remoteVersion,
            localUpdatedAt = meta.localUpdatedAt,
            remoteUpdatedAt = meta.remoteUpdatedAt,
            lastFirestoreSyncAt = meta.lastFirestoreSyncAt,
            lastDriveBackupAt = meta.lastDriveBackupAt,
            isDirty = if (meta.isDirty) 1 else 0,
            hasConflict = if (meta.hasConflict) 1 else 0,
            lastSyncError = meta.lastSyncError,
            retryCount = meta.retryCount
        )
    }
}
