package com.meadow.feature.common.api

data class FeatureContext(
    val projectId: String? = null,
    val seriesId: String? = null,
    val scriptId: String? = null,
    val catalogItemId: String? = null,
    val enabledFeatures: Set<String> = emptySet()
)