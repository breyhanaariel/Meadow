package com.meadow.feature.calendar.domain.usecase

import com.meadow.feature.calendar.sync.CalendarSyncRunner
import javax.inject.Inject

class SyncCalendarUseCase @Inject constructor(
    private val runner: CalendarSyncRunner
) {

    suspend operator fun invoke(): Boolean {
        return runner.runSync()
    }
}
