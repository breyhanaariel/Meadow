package com.meadow.feature.project.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController


interface ProjectFeatureHomeProvider {

    val featureKey: String get() = "project"
    @Composable
    fun HomeContentOrNull(
        navController: NavHostController
    )
}
