package com.meadow.core.common.preferences

import kotlinx.coroutines.flow.Flow


interface FeaturePreferencesProvider {
    val enabledFeatures: Flow<Set<String>>
}
