package com.meadow.feature.catalog.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface CatalogFeatureHomeProvider {

    val featureKey: String get() = "catalog"

    @Composable
    fun HomeContentOrNull(
        navController: NavHostController
    )
}
