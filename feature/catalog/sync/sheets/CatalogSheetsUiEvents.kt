package com.meadow.feature.catalog.sync.sheets

sealed interface CatalogSheetsUiEvent {
    data class Progress(val message: String) : CatalogSheetsUiEvent
    data class Success(val message: String) : CatalogSheetsUiEvent
    data class Error(val message: String) : CatalogSheetsUiEvent
}
