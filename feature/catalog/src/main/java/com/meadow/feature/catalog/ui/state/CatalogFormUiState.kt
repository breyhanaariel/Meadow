package com.meadow.feature.catalog.ui.state

import com.meadow.core.data.fields.FieldWithValue
import com.meadow.feature.catalog.domain.model.CatalogType
import com.meadow.feature.project.domain.model.ProjectType
import java.util.UUID

/* ─── SHARED CREATE + EDIT FORM ────────────────────────────── */

sealed interface CatalogFormState {
    val fields: List<FieldWithValue>
    val seriesId: String?
    val seriesSharedFieldIds: Set<String>
    val error: String?

    fun copyForm(
        fields: List<FieldWithValue> = this.fields,
        seriesId: String? = this.seriesId,
        seriesSharedFieldIds: Set<String> = this.seriesSharedFieldIds,
        error: String? = this.error
    ): CatalogFormState
}

/* ─── CREATE ────────────────────────────── */
data class CreateCatalogItemUiState(
    override val fields: List<FieldWithValue> = emptyList(),
    override val seriesId: String? = null,
    override val seriesSharedFieldIds: Set<String> = emptySet(),
    override val error: String? = null,
    val catalogType: CatalogType? = null,
    val projectType: ProjectType? = null,
    val schemaId: String? = null,
    val isSaving: Boolean = false,
    val saveCompleted: Boolean = false,
    val draftItemId: String = UUID.randomUUID().toString()
) : CatalogFormState {

    override fun copyForm(
        fields: List<FieldWithValue>,
        seriesId: String?,
        seriesSharedFieldIds: Set<String>,
        error: String?
    ): CreateCatalogItemUiState = copy(
        fields = fields,
        seriesId = seriesId,
        seriesSharedFieldIds = seriesSharedFieldIds,
        error = error
    )

    fun field(id: String): FieldWithValue =
        fields.firstOrNull { it.definition.id == id }
            ?: error("CreateCatalogItemUiState missing required field: $id")

    fun fieldOrNull(id: String): FieldWithValue? =
        fields.firstOrNull { it.definition.id == id }
}


/* ─── EDIT ────────────────────────────── */
data class EditCatalogItemUiState(
    override val fields: List<FieldWithValue> = emptyList(),
    override val seriesId: String? = null,
    override val seriesSharedFieldIds: Set<String> = emptySet(),
    override val error: String? = null,
    val projectId: String = "",
    val catalogItemId: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val showDeleteConfirm: Boolean = false
) : CatalogFormState {

    override fun copyForm(
        fields: List<FieldWithValue>,
        seriesId: String?,
        seriesSharedFieldIds: Set<String>,
        error: String?
    ): EditCatalogItemUiState = copy(
        fields = fields,
        seriesId = seriesId,
        seriesSharedFieldIds = seriesSharedFieldIds,
        error = error
    )
}
