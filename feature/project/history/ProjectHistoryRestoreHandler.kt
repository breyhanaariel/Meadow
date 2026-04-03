package com.meadow.feature.project.history

import com.meadow.core.domain.model.HistoryEntry
import com.meadow.core.domain.model.HistoryOwnerType
import com.meadow.feature.common.api.HistoryRestoreHandler
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import javax.inject.Inject

class ProjectHistoryRestoreHandler @Inject constructor(
    private val projectRepository: ProjectRepositoryContract
) : HistoryRestoreHandler {

    override fun supports(ownerType: HistoryOwnerType): Boolean =
        ownerType == HistoryOwnerType.PROJECT

    override suspend fun restore(entry: HistoryEntry) {
        when (entry.fieldId) {
            "project.seriesId" -> {
                projectRepository.updateProjectSeries(
                    projectId = entry.ownerId,
                    seriesId = entry.oldValue
                )
            }

            else -> {
                projectRepository.updateProjectField(
                    projectId = entry.ownerId,
                    fieldKey = entry.fieldId,
                    value = entry.oldValue
                )
            }
        }
    }
}