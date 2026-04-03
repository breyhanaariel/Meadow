package com.meadow.feature.project.domain.model

import kotlinx.coroutines.flow.Flow

interface ProjectFeatureCountProvider {
    val key: String

    fun observeCount(projectId: String): Flow<Int>
}