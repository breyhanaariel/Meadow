package com.meadow.feature.catalog.domain.usecase

import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogScope
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ObserveCatalogUseCase @Inject constructor(
    private val catalogRepo: CatalogRepositoryContract,
    private val projectRepo: ProjectRepositoryContract
) {

    operator fun invoke(
        scope: CatalogScope
    ): Flow<List<CatalogItem>> =
        when (scope) {

            CatalogScope.Global ->
                catalogRepo.observeAllCatalog()

            is CatalogScope.Project ->
                catalogRepo.observeByProject(scope.projectId)

            is CatalogScope.Series -> {
                val projectIdsInSeries: Flow<Set<String>> =
                    projectRepo.observeAllProjects()
                        .map { projects ->
                            projects
                                .filter { it.seriesId == scope.seriesId }
                                .map { it.id }
                                .toSet()
                        }

                combine(
                    catalogRepo.observeAllCatalog(),
                    projectIdsInSeries
                ) { allItems, projectIds ->
                    allItems.filter { item ->
                        item.seriesId == scope.seriesId ||
                                (item.projectId != null && item.projectId in projectIds)
                    }
                }
            }
        }
}
