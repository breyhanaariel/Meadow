package com.meadow.feature.catalog.internal.schema

import com.meadow.feature.catalog.domain.model.CatalogType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogSchemaRegistry @Inject constructor(
    private val loader: CatalogSchemaLoader
) {

    private val schemas = linkedMapOf<String, CatalogSchema>()
    private var initialized: Boolean = false

    fun initializeIfNeeded() {
        if (initialized) return
        initialized = true

        CatalogSchemaDefinitions.ALL.forEach { def ->
            val fields = loader.loadFields(def.assetPath)
            val schema = CatalogSchema(
                schemaId = def.schemaId,
                labelKey = def.labelKey,
                iconResId = def.iconResId,
                assetPath = def.assetPath,
                primaryFieldKey = def.primaryFieldKey,
                appliesToProjectTypeKeys = def.appliesToProjectTypeKeys,
                fields = fields
            )
            schemas[def.schemaId] = schema
        }
    }

    fun getSchema(schemaId: String): CatalogSchema? {
        initializeIfNeeded()
        return schemas[schemaId]
    }

    fun getAllSchemas(): List<CatalogSchema> {
        initializeIfNeeded()
        return schemas.values.toList()
    }

    fun getSchemasForProjectType(projectTypeKey: String?): List<CatalogSchema> {
        initializeIfNeeded()
        val key = projectTypeKey?.trim().orEmpty()
        if (key.isBlank()) return schemas.values.toList()

        return schemas.values.filter { schema ->
            schema.appliesToProjectTypeKeys.contains(key)
        }
    }

    fun getSchemasForType(typeKey: String): List<CatalogSchema> {
        initializeIfNeeded()
        return schemas.values.filter {
            it.schemaId == typeKey ||
                    it.schemaId.startsWith("${typeKey}_")
        }
    }
    fun getCatalogItemSchemas(): List<CatalogSchema> {
        initializeIfNeeded()

        val validTypeIds = CatalogType.entries
            .map { it.name.lowercase() }
            .toSet()

        return schemas.values.filter { schema ->
            schema.schemaId in validTypeIds
        }
    }
    fun getCatalogItemSchemasForProject(projectTypeKey: String): List<CatalogSchema> {
        initializeIfNeeded()

        val validTypeIds = CatalogType.entries
            .map { it.name.lowercase() }
            .toSet()

        return schemas.values.filter { schema ->
            schema.schemaId in validTypeIds &&
                    schema.appliesToProjectTypeKeys.contains(projectTypeKey)
        }
    }


}