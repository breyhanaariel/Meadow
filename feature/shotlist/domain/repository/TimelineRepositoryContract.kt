package com.meadow.feature.timeline.domain.repository

import com.meadow.core.common.functional.ResultX
import com.meadow.feature.timeline.domain.model.TimelineEvent
import kotlinx.coroutines.flow.Flow

interface TimelineRepositoryContract {
    fun getTimeline(projectId: String): Flow<ResultX<List<TimelineEvent>>>
}
