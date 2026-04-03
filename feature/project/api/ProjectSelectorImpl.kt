package com.meadow.feature.project.api

import androidx.compose.ui.res.stringResource
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.ui.util.readTitleOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ProjectSelectorImpl @Inject constructor(
    private val repo: ProjectRepositoryContract
) : ProjectSelector {

    override fun observeAvailableProjects(): Flow<List<ProjectSelectorItem>> =
        repo.observeAllProjects()
            .map { projects ->
                projects.map { project ->
                    ProjectSelectorItem(
                        id = project.id,
                        title = project.readTitleOrNull(),
                            typeKey = project.type.name.lowercase()
                    )
                }
            }
}
