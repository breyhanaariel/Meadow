package com.meadow.feature.project.domain.repository

import com.meadow.feature.project.domain.model.ProjectFieldHistory
import kotlinx.coroutines.flow.Flow

interface ProjectFieldHistoryContract {

    fun observeProjectHistory(
        projectId: String
    ): Flow<List<ProjectFieldHistory>>

    fun observeFieldHistory(
        projectId: String,
        fieldId: String
    ): Flow<List<ProjectFieldHistory>>

    suspend fun recordFieldChange(
        projectId: String,
        fieldId: String,
        oldValue: String?,
        newValue: String?
    )

    suspend fun clearProjectHistory(projectId: String)

    suspend fun clearFieldHistory(projectId: String, fieldId: String)
}
