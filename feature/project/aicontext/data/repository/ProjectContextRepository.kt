package com.meadow.feature.project.aicontext.data.repository

import com.meadow.feature.project.ui.components.AiFieldHelperScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectContextRepository @Inject constructor(
    private val aiContextRepository: AiContextRepository
) {

    fun buildContextText(scope: AiFieldHelperScope): String {
        return aiContextRepository.buildContextText(scope)
    }

    fun readProjectPrompts(): String {
        return aiContextRepository.readProjectPrompts()
    }

    fun readSeriesPrompts(): String {
        return aiContextRepository.readSeriesPrompts()
    }
}
