package com.meadow.feature.catalog.ui.util

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.meadow.feature.catalog.internal.schema.CatalogSchema
import com.meadow.feature.catalog.internal.schema.CatalogSchemaRegistry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class CatalogSchemaUiResolver @Inject constructor(
    private val registry: CatalogSchemaRegistry
) {

    fun getSchema(schemaId: String): CatalogSchema? =
        registry.getSchema(schemaId)

    fun allSchemas(): List<CatalogSchema> =
        registry.getAllSchemas()

    fun catalogItemSchemas(): List<CatalogSchema> =
        registry.getCatalogItemSchemas()

    fun catalogItemSchemasForProject(projectTypeKey: String): List<CatalogSchema> =
        registry.getCatalogItemSchemasForProject(projectTypeKey)


    fun labelKey(schemaId: String): String? =
        registry.getSchema(schemaId)?.labelKey

    @DrawableRes
    fun iconRes(schemaId: String): Int? =
        registry.getSchema(schemaId)?.iconResId

    fun primaryFieldKey(schemaId: String): String? =
        registry.getSchema(schemaId)?.primaryFieldKey


    fun resolveLabel(context: Context, schemaId: String): String {
        val key = labelKey(schemaId)?.trim().orEmpty()
        if (key.isBlank()) return ""

        val resId = context.resources.getIdentifier(
            key,
            "string",
            context.packageName
        )
        return if (resId != 0) context.getString(resId) else key
    }
}

@HiltViewModel
class CatalogSchemaUiResolverViewModel @Inject constructor(
    val resolver: CatalogSchemaUiResolver
) : ViewModel()