package com.meadow.feature.catalog.sync.sheets

import com.meadow.core.google.api.sheets.SheetsApi
import com.meadow.core.google.api.sheets.model.ValueRange
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogSheetsRepository @Inject constructor(
    private val api: SheetsApi
) {

    suspend fun readSheet(
        spreadsheetId: String,
        sheetName: String
    ): List<List<String>> {
        val range = "$sheetName!A1:Z"
        return api.readRange(spreadsheetId, range)
            .body()
            ?.values
            ?: emptyList()
    }

    suspend fun writeSheet(
        spreadsheetId: String,
        sheetName: String,
        rows: List<List<String>>
    ) {
        val range = "$sheetName!A1"
        api.writeRange(
            spreadsheetId = spreadsheetId,
            range = range,
            body = ValueRange(range = range, values = rows)
        )
    }
}
