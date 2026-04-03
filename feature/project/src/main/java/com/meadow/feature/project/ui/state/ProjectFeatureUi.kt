package com.meadow.feature.project.ui.state

import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences

data class ProjectFeatureUi(
    val titleRes: Int,
    val route: String,
    val icon: @Composable () -> Unit,
    val key: Preferences.Key<Boolean>,
    val enabled: Boolean,
    val count: Int
)