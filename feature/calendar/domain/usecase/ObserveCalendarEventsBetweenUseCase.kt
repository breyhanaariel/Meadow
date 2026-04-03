package com.meadow.feature.calendar.domain.usecase

import com.meadow.feature.calendar.domain.model.CalendarEvent
import com.meadow.feature.calendar.domain.repository.CalendarRepositoryContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCalendarEventsBetweenUseCase @Inject constructor(
    private val repo: CalendarRepositoryContract
) {

    operator fun invoke(
        fromMillis: Long,
        toMillis: Long
    ): Flow<List<CalendarEvent>> {
        return repo.observeEventsBetween(fromMillis, toMillis)
    }
}
