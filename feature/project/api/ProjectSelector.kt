package com.meadow.feature.project.api

import kotlinx.coroutines.flow.Flow


interface ProjectSelector {
    fun observeAvailableProjects(): Flow<List<ProjectSelectorItem>>
}

data class ProjectSelectorItem(
    val id: String,
    val title: String?,
    val typeKey: String
)
