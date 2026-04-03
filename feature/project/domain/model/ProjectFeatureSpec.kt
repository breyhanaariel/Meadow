package com.meadow.feature.project.domain.model

import androidx.compose.runtime.Composable

interface ProjectFeatureSpec {
    val key: String
    val titleRes: Int
    val icon: @Composable () -> Unit
    fun routeForProject(projectId: String): String
}
