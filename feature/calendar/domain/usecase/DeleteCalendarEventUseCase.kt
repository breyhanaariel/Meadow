package com.meadow.feature.calendar.domain.usecase

import com.meadow.feature.calendar.domain.repository.CalendarRepositoryContract
import javax.inject.Inject

class DeleteCalendarEventUseCase @Inject constructor(
    private val repo: CalendarRepositoryContract
) {

    suspend operator fun invoke(localId: String) {
        repo.markDeleted(localId)
    }
}
