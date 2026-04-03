package com.meadow.feature.project.ui.state

import androidx.compose.runtime.Composable
import com.meadow.feature.project.domain.model.Project
data class ProjectDashboardUiState(
    val isLoading: Boolean = true,
    val project: Project? = null,
    val title: String = "",
    val projectTypeLine: String = "",
    val description: String = "",
    val coverImagePath: String? = null,
    val smallLine: String = "",
    val formattedFields: List<ProjectFormattedField> = emptyList(),
    val overview: ProjectOverviewUi = ProjectOverviewUi(),
    val chips: ProjectChipsUi = ProjectChipsUi(),
    val warnings: List<String> = emptyList(),
    val sections: List<ProjectDashboardSection> = emptyList(),
    val updatedAt: Long? = null,
    val sync: ProjectSyncUiState = ProjectSyncUiState(),
    val errorMessage: String? = null,
    val showDeleteConfirm: Boolean = false,
    val featureCounts: Map<String, Int> = emptyMap()
)

data class ProjectOverviewUi(
    val pitch: String? = null,
    val premise: String? = null,
    val promise: String? = null,
    val plot: String? = null
)

data class ProjectChipsUi(
    val audience: List<String> = emptyList(),
    val genre: List<String> = emptyList(),
    val elements: List<String> = emptyList(),
    val rating: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)

sealed interface ProjectDashboardSection {
    val id: String
}

data class ProjectDashboardCardSection(
    override val id: String,
    val titleRes: Int,
    val route: String,
    val content: @Composable () -> Unit
) : ProjectDashboardSection

data class ProjectFormattedField(
    val id: String,
    val label: String,
    val value: String,
    val isEmphasized: Boolean = false,
    val isMultiline: Boolean = false
)