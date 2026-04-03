package com.meadow.feature.timeline.repository

import com.meadow.core.common.functional.ResultX
import com.meadow.feature.timeline.model.TimelineEvent
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import com.meadow.core.common.functional.ResultX.Companion.success

@Singleton
class TimelineRepository @Inject constructor() : TimelineRepositoryContract {

    private val map = mutableMapOf<String, MutableStateFlow<List<TimelineEvent>>>()

    override fun getTimeline(projectId: String): Flow<ResultX<List<TimelineEvent>>> =
        map.getOrPut(projectId) { MutableStateFlow(emptyList()) }
            .map { ResultX.success(it) }
}
