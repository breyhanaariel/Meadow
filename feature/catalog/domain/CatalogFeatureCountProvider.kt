package com.meadow.feature.catalog.domain

import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import com.meadow.feature.project.domain.model.ProjectFeatureCountProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class CatalogFeatureCountProvider @Inject constructor(
    private val repository: CatalogRepositoryContract
) : ProjectFeatureCountProvider {

    override val key: String = "catalog"

    override fun observeCount(projectId: String): Flow<Int> {
        return repository.observeCountForProject(projectId)
    }
}