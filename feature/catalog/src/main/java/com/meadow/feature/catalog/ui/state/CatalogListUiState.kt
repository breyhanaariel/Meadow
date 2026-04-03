package com.meadow.feature.catalog.ui.state

import androidx.annotation.StringRes
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogType

data class CatalogListUiState(
    val allItems: List<CatalogItemUiModel> = emptyList(),
    val items: List<CatalogItemUiModel> = emptyList(),
    val searchQuery: String = "",
    val sortMode: CatalogListSort = CatalogListSort.UPDATED_DESC,
    val viewMode: CatalogListViewMode = CatalogListViewMode.GRID,
    val scope: CatalogListScope = CatalogListScope.Global,
    val filters: CatalogListFilters = CatalogListFilters(),
    val filterOptions: CatalogFilterOptions,
    val pendingDeleteId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class CatalogListViewMode {
    GRID,
    LIST,
}

enum class CatalogListSort(@StringRes val labelRes: Int) {
    TITLE_ASC(CoreUiR.string.sort_title_asc),
    TITLE_DESC(CoreUiR.string.sort_title_desc),
    CREATED_ASC(CoreUiR.string.sort_created_asc),
    CREATED_DESC(CoreUiR.string.sort_created_desc),
    UPDATED_ASC(CoreUiR.string.sort_updated_asc),
    UPDATED_DESC(CoreUiR.string.sort_updated_desc);
}


data class CatalogListFilters(
    val types: Set<CatalogType> = emptySet()
)


data class CatalogFilterOptions(
    val types: List<CatalogType>
)

fun CatalogListFilters.isActive(): Boolean =
    types.isNotEmpty()

sealed class CatalogListScope {
    data object Global : CatalogListScope()
    data class Project(val projectId: String) : CatalogListScope()
    data class Series(val seriesId: String) : CatalogListScope()
}
