package com.meadow.feature.calendar.sync

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarSyncRunner @Inject constructor(
    private val remoteSync: CalendarRemoteSync
) {

    suspend fun runSync(): Boolean {
        return remoteSync.sync()
    }
}
