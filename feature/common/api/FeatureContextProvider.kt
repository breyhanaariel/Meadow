package com.meadow.feature.common.api

import kotlinx.coroutines.flow.StateFlow

interface FeatureContextProvider {
    val context: StateFlow<FeatureContext>
}