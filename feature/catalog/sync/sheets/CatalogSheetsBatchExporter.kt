package com.meadow.feature.catalog.sync.sheets

import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.internal.schema.CatalogSchemaRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogSheetsBatchExporter @Inject constructor(
    private val sheetsRepo: CatalogSheetsRepository,
    private val schemaRegistry: CatalogSchemaRegistry
) {

    suspend fun exportBatch(
        spreadsheetId: String,
        items: List<CatalogItem>
    ) {
        items
            .groupBy {
                CatalogSheetsSheetResolver.resolveSheetName(
                    it.projectId,
                    it.seriesId
                )
            }
            .forEach { (_, grouped) ->
                val schema = schemaRegistry.getSchema(grouped.first().schemaId)
                    ?: return@forEach

                val header = CatalogSheetsRowMapper.header(schema)
                val existing = sheetsRepo.readSheet(spreadsheetId, grouped.first().let {
                    CatalogSheetsSheetResolver.resolveSheetName(it.projectId, it.seriesId)
                })

                var merged = existing
                grouped.forEach { item ->
                    val row = CatalogSheetsRowMapper.row(item, schema)
                    merged = CatalogSheetsRowUpserter.upsert(
                        merged,
                        header,
                        row
                    )
                }

                sheetsRepo.writeSheet(
                    spreadsheetId,
                    CatalogSheetsSheetResolver.resolveSheetName(
                        grouped.first().projectId,
                        grouped.first().seriesId
                    ),
                    merged
                )
            }
    }
}
