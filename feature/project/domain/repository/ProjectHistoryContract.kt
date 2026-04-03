package com.meadow.feature.project.domain.repository

import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.ProjectHistoryItem
import kotlinx.coroutines.flow.Flow

interface ProjectHistoryContract {

    fun observeProjectHistory(
        projectId: String
    ): Flow<List<ProjectHistoryItem>>

    suspend fun restoreProjectFromHistory(
        historyId: String
    ): Project?
}