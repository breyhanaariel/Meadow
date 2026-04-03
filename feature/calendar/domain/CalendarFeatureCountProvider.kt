package com.meadow.feature.calendar.domain

import com.meadow.feature.calendar.domain.repository.CalendarRepositoryContract
import com.meadow.feature.project.domain.model.ProjectFeatureCountProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class CalendarFeatureCountProvider @Inject constructor(
    private val repository: CalendarRepositoryContract
) : ProjectFeatureCountProvider {

    override val key: String = "calendar"

    override fun observeCount(projectId: String): Flow<Int> {
        return repository.observeCountForProject(projectId)
    }
}