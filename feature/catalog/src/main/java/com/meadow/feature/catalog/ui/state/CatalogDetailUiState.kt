package com.meadow.feature.catalog.ui.state

import com.meadow.feature.catalog.domain.model.CatalogItem

data class CatalogDetailUiState(
    val isLoading: Boolean = true,
    val item: CatalogItem? = null,
    val error: String? = null,
    val isSaving: Boolean = false
)
