package com.meadow.feature.calendar.domain.usecase

import com.meadow.feature.calendar.domain.model.CalendarEvent
import com.meadow.feature.calendar.domain.repository.CalendarRepositoryContract
import javax.inject.Inject

class CreateOrUpdateCalendarEventUseCase @Inject constructor(
    private val repo: CalendarRepositoryContract
) {

    suspend operator fun invoke(event: CalendarEvent): String {
        return repo.createOrUpdateEvent(event)
    }
}
