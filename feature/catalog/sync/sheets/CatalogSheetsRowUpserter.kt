package com.meadow.feature.catalog.sync.sheets

object CatalogSheetsRowUpserter {

    fun upsert(
        existing: List<List<String>>,
        header: List<String>,
        newRow: List<String>
    ): List<List<String>> {

        if (existing.isEmpty()) {
            return listOf(header, newRow)
        }

        val rows = existing.toMutableList()
        val idIndex = CatalogSheetsConstants.ID_COLUMN_INDEX
        val newId = newRow[idIndex]

        val dataStart = 1
        val foundIndex = rows
            .drop(dataStart)
            .indexOfFirst { it.getOrNull(idIndex) == newId }

        return if (foundIndex >= 0) {
            rows[dataStart + foundIndex] = newRow
            rows
        } else {
            rows.add(newRow)
            rows
        }
    }
}
