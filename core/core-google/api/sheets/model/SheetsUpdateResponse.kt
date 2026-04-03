package com.meadow.core.google.api.sheets.model

data class SheetsUpdateResponse(
    val spreadsheetId: String?,
    val updatedCells: Int?
)