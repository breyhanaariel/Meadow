package com.meadow.feature.project.ui.state

import com.meadow.core.data.fields.FieldWithValue
import com.meadow.feature.project.domain.model.ProjectType
import java.util.UUID

sealed interface ProjectFormState {

    val projectType: ProjectType?
    val fields: List<FieldWithValue>
    val seriesId: String?
    val seriesSharedFieldIds: Set<String>
    val error: String?

    fun copyForm(
        projectType: ProjectType? = this.projectType,
        fields: List<FieldWithValue> = this.fields,
        seriesId: String? = this.seriesId,
        seriesSharedFieldIds: Set<String> = this.seriesSharedFieldIds,
        error: String? = this.error
    ): ProjectFormState
}

data class CreateProjectUiState(
    override val projectType: ProjectType? = null,
    override val fields: List<FieldWithValue> = emptyList(),
    override val seriesId: String? = null,
    override val seriesSharedFieldIds: Set<String> = emptySet(),
    override val error: String? = null,
    val isSaving: Boolean = false,
    val saveCompleted: Boolean = false,
    val draftProjectId: String = UUID.randomUUID().toString()
) : ProjectFormState {

    override fun copyForm(
        projectType: ProjectType?,
        fields: List<FieldWithValue>,
        seriesId: String?,
        seriesSharedFieldIds: Set<String>,
        error: String?
    ): CreateProjectUiState = copy(
        projectType = projectType,
        fields = fields,
        seriesId = seriesId,
        seriesSharedFieldIds = seriesSharedFieldIds,
        error = error
    )

    fun field(id: String): FieldWithValue =
        fields.firstOrNull { it.definition.id == id }
            ?: error("CreateProjectUiState missing required field: $id")

    fun fieldOrNull(id: String): FieldWithValue? =
        fields.firstOrNull { it.definition.id == id }
}

data class EditProjectUiState(
    override val projectType: ProjectType? = null,
    override val fields: List<FieldWithValue> = emptyList(),
    override val seriesId: String? = null,
    override val seriesSharedFieldIds: Set<String> = emptySet(),
    override val error: String? = null,
    val isLoading: Boolean = true,
    val projectId: String = "",
    val sync: ProjectSyncUiState = ProjectSyncUiState(),
    val showDeleteConfirm: Boolean = false,
    val isSaving: Boolean = false,
    val hasUnsavedChanges: Boolean = false
) : ProjectFormState {

    override fun copyForm(
        projectType: ProjectType?,
        fields: List<FieldWithValue>,
        seriesId: String?,
        seriesSharedFieldIds: Set<String>,
        error: String?
    ): EditProjectUiState = copy(
        projectType = projectType,
        fields = fields,
        seriesId = seriesId,
        seriesSharedFieldIds = seriesSharedFieldIds,
        error = error
    )
}