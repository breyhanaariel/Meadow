package com.meadow.feature.catalog.sync.sheets

import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.internal.schema.CatalogSchema

object CatalogSheetsRowMapper {

    fun header(schema: CatalogSchema): List<String> =
        buildList {
            addAll(
                listOf(
                    "id",
                    "schemaId",
                    "projectId",
                    "seriesId",
                    "createdAt",
                    "updatedAt"
                )
            )
            addAll(schema.fields.map { it.key })
        }

    fun row(item: CatalogItem, schema: CatalogSchema): List<String> =
        buildList {
            add(item.id)
            add(item.schemaId)
            add(item.projectId.orEmpty())
            add(item.seriesId.orEmpty())
            add(item.createdAt.toString())
            add(item.updatedAt.toString())

            val byKey = item.fields.associateBy { it.definition.key }
            schema.fields.forEach { def ->
                add(byKey[def.key]?.value?.rawValue ?: "")
            }
        }
}
