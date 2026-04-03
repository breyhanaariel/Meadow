package com.meadow.feature.script.api

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

interface ScriptFeatureHomeProvider {

    val featureKey: String get() = "script"

    @Composable
    fun HomeContentOrNull(
        navController: NavHostController
    )
}
