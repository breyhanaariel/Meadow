package com.meadow.feature.catalog.api

import android.content.Context
import com.meadow.core.ui.state.ReferenceDataProvider
import com.meadow.core.ui.state.ReferenceItem
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import com.meadow.feature.catalog.internal.schema.CatalogSchemaRegistry
import com.meadow.feature.common.state.FeatureContextState
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class CatalogReferenceDataProvider @Inject constructor(
    private val catalogRepository: CatalogRepositoryContract,
    private val schemaRegistry: CatalogSchemaRegistry,
    private val featureContextState: FeatureContextState,
    @ApplicationContext private val context: Context
) : ReferenceDataProvider {

    private fun resolveIfStringResource(value: String): String {
        val resId = context.resources.getIdentifier(
            value,
            "string",
            context.packageName
        )

        return if (resId != 0) {
            context.getString(resId)
        } else {
            value
        }
    }

    override fun observeItems(schemaKey: String): Flow<List<ReferenceItem>> {
        val schema = schemaRegistry.getSchema(schemaKey)
            ?: error("Unknown schema: $schemaKey")

        val projectId = featureContextState.context.value.projectId
            ?: error("Reference picker requires an active projectId in FeatureContextState")

        val primaryKey = schema.primaryFieldKey

        return catalogRepository
            .observeByProject(projectId)
            .map { allItems ->
                allItems
                    .filter { it.schemaId == schemaKey }
                    .map { item ->
                        val rawLabel: String =
                            item.fields
                                .firstOrNull { fwv -> fwv.definition.key == primaryKey }
                                ?.value
                                ?.rawValue
                                ?.takeIf { it.isNotBlank() }
                                ?: "Untitled"

                        val label = resolveIfStringResource(rawLabel)
                        ReferenceItem(
                            id = item.id,
                            label = label
                        )
                    }
            }
    }
}
