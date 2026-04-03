package com.meadow.feature.project.data.repository

import com.meadow.feature.project.data.local.ProjectFieldHistoryDao
import com.meadow.feature.project.data.local.ProjectFieldHistoryEntity
import com.meadow.feature.project.domain.model.ChangeSource
import com.meadow.feature.project.domain.model.ProjectFieldHistory
import com.meadow.feature.project.domain.repository.ProjectFieldHistoryContract
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectFieldHistoryRepository @Inject constructor(
    private val historyDao: ProjectFieldHistoryDao
) : ProjectFieldHistoryContract {

    override fun observeProjectHistory(projectId: String) =
        historyDao
            .observeProjectHistory(projectId)
            .map { entities -> entities.map { it.toDomain() } }

    override fun observeFieldHistory(
        projectId: String,
        fieldId: String
    ) =
        historyDao
            .observeFieldHistory(projectId, fieldId)
            .map { entities -> entities.map { it.toDomain() } }

    override suspend fun recordFieldChange(
        projectId: String,
        fieldId: String,
        oldValue: String?,
        newValue: String?
    ) {
        if (oldValue == newValue) return

        historyDao.insert(
            ProjectFieldHistoryEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                field = fieldId,
                oldValue = oldValue,
                newValue = newValue,
                changedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun clearProjectHistory(projectId: String) {
        historyDao.deleteByProjectId(projectId)
    }

    override suspend fun clearFieldHistory(
        projectId: String,
        fieldId: String
    ) {
        historyDao.deleteByProjectAndField(projectId, fieldId)
    }
}

private fun ProjectFieldHistoryEntity.toDomain(): ProjectFieldHistory =
    ProjectFieldHistory(
        projectId = projectId,
        fieldId = field,
        oldValue = oldValue,
        newValue = newValue,
        changedAt = changedAt,
        source = ChangeSource.MANUAL,
        isSeriesField = false
    )
