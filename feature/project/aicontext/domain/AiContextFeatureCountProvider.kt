package com.meadow.feature.project.aicontext.domain

import com.meadow.feature.project.domain.model.ProjectFeatureCountProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Singleton
class AiContextFeatureCountProvider @Inject constructor() : ProjectFeatureCountProvider {

    override val key: String = "ai_context"

    override fun observeCount(projectId: String): Flow<Int> {
        return flowOf(0)
    }
}
