package com.meadow.feature.catalog.ui.state

import androidx.annotation.DrawableRes
import com.meadow.feature.catalog.domain.model.CatalogType

data class CatalogItemUiModel(
    val id: String,
    val schemaId: String,
    val projectId: String?,
    val seriesId: String?,
    val title: String?,
    val type: CatalogType,
    @DrawableRes val iconResId: Int,
    val schemaLabel: String,
    val createdAt: Long,
    val updatedAt: Long,
    val searchBlob: String = ""
)
