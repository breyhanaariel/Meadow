package com.meadow.feature.calendar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CalendarEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CalendarDatabase : RoomDatabase() {
    abstract fun calendarDao(): CalendarDao
}
