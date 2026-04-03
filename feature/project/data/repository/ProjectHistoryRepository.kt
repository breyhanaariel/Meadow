package com.meadow.feature.project.data.repository

import com.meadow.feature.project.data.local.ProjectHistoryDao
import com.meadow.feature.project.data.local.ProjectHistoryEntity
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.ProjectHistoryAction
import com.meadow.feature.project.domain.model.ProjectHistoryItem
import com.meadow.feature.project.domain.repository.ProjectHistoryContract
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID
import com.meadow.core.data.di.GsonModule


@Singleton
class ProjectHistoryRepository @Inject constructor(
    private val dao: ProjectHistoryDao,
    private val gson: Gson
) : ProjectHistoryContract {

    override fun observeProjectHistory(projectId: String) =
        dao.observeHistory(projectId).map { list ->
            list.map {
                ProjectHistoryItem(
                    id = it.id,
                    projectId = it.projectId,
                    action = ProjectHistoryAction.valueOf(it.action),
                    timestamp = it.timestamp,
                    summary = it.summary
                )
            }
        }

    suspend fun recordSnapshot(
        project: Project,
        action: ProjectHistoryAction,
        summary: String?
    ) {
        dao.insert(
            ProjectHistoryEntity(
                id = UUID.randomUUID().toString(),
                projectId = project.id,
                action = action.name,
                timestamp = System.currentTimeMillis(),
                snapshotJson = gson.toJson(project),
                summary = summary
            )
        )
    }

    override suspend fun restoreProjectFromHistory(historyId: String): Project? =
        dao.getById(historyId)?.let {
            gson.fromJson(it.snapshotJson, Project::class.java)
        }
}