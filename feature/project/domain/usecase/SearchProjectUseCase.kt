package com.meadow.feature.project.domain.usecase

import com.meadow.feature.project.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface SearchProjectsUseCase {
    fun search(query: String): Flow<List<Project>>
}
