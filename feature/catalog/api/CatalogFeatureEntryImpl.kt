package com.meadow.feature.catalog.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.meadow.feature.common.state.FeatureContextState
import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.catalog.ui.navigation.registerCatalogNavGraph
import com.meadow.feature.catalog.ui.util.CatalogSchemaBootstrap
import javax.inject.Inject

class CatalogFeatureEntryImpl @Inject constructor(
    private val featureContextState: FeatureContextState,
    private val schemaBootstrap: CatalogSchemaBootstrap
) : FeatureEntry {

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController
    ) {
        navGraphBuilder.registerCatalogNavGraph(
            navController = navController,
            featureContextState = featureContextState,
            schemaBootstrap = schemaBootstrap
        )
    }
}
