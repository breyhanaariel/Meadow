package com.meadow.feature.catalog.data.mappers

import com.meadow.core.data.fields.FieldValue
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.feature.catalog.data.local.CatalogFieldValueEntity
import com.meadow.feature.catalog.data.local.CatalogItemEntity
import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogSyncMeta
import com.meadow.feature.catalog.internal.schema.CatalogSchema

object CatalogItemMapper {

    fun toDomainLite(entity: CatalogItemEntity): CatalogItem =
        CatalogItem(
            id = entity.id,
            schemaId = entity.schemaId,
            projectId = entity.projectId,
            seriesId = entity.seriesId,
            fields = emptyList(),
            primaryText = entity.primaryText,
            searchBlob = entity.searchBlob,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            syncMeta = CatalogSyncMeta()

        )

    fun toDomainFull(
        item: CatalogItemEntity,
        fieldEntities: List<CatalogFieldValueEntity>,
        schema: CatalogSchema
    ): CatalogItem {
        val valuesById = fieldEntities.associateBy { it.fieldId }

        val fields = schema.fields.map { def ->
            FieldWithValue(
                definition = def,
                value = FieldValue(
                    fieldId = def.id,
                    ownerItemId = item.id,
                    rawValue = valuesById[def.id]?.rawValue
                )
            )
        }

        return CatalogItem(
            id = item.id,
            schemaId = item.schemaId,
            projectId = item.projectId,
            seriesId = item.seriesId,
            fields = fields,
            primaryText = item.primaryText,
            searchBlob = item.searchBlob,
            createdAt = item.createdAt,
            updatedAt = item.updatedAt,
            syncMeta = CatalogSyncMeta()
        )
    }

    fun toEntity(item: CatalogItem, schema: CatalogSchema): CatalogItemEntity =
        CatalogItemEntity(
            id = item.id,
            schemaId = item.schemaId,
            projectId = item.projectId,
            seriesId = item.seriesId,
            primaryText = computePrimaryText(item, schema),
            searchBlob = computeSearchBlob(item),
            createdAt = item.createdAt,
            updatedAt = item.updatedAt
        )

    fun toFieldEntities(item: CatalogItem): List<CatalogFieldValueEntity> =
        item.fields.map {
            CatalogFieldValueEntity(
                catalogItemId = item.id,
                fieldId = it.definition.id,
                rawValue = it.value?.rawValue
            )
        }

    private fun computePrimaryText(item: CatalogItem, schema: CatalogSchema): String? {
        val key = schema.primaryFieldKey ?: return null
        return item.fields
            .firstOrNull { it.definition.key == key }
            ?.value
            ?.rawValue
            ?.takeIf { it.isNotBlank() }
    }

    private fun computeSearchBlob(item: CatalogItem): String =
        item.fields
            .mapNotNull { it.value?.rawValue?.takeIf(String::isNotBlank) }
            .joinToString("\n")
}
