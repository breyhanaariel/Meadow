package com.meadow.feature.calendar.di

import android.content.Context
import androidx.room.Room
import com.meadow.feature.calendar.api.CalendarFeatureEntryImpl
import com.meadow.feature.calendar.data.local.CalendarDao
import com.meadow.feature.calendar.data.local.CalendarDatabase
import com.meadow.feature.calendar.data.repository.CalendarRepository
import com.meadow.feature.calendar.domain.repository.CalendarRepositoryContract
import com.meadow.feature.common.ui.navigation.FeatureEntry
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CalendarModule {

    @Binds
    @Singleton
    abstract fun bindCalendarRepository(
        impl: CalendarRepository
    ): CalendarRepositoryContract

    @Binds
    @Singleton
    abstract fun bindCalendarFeatureEntry(
        impl: CalendarFeatureEntryImpl
    ): FeatureEntry
}

@Module
@InstallIn(SingletonComponent::class)
object CalendarProvidesModule {

    @Provides
    @Singleton
    fun provideCalendarDatabase(
        @ApplicationContext context: Context
    ): CalendarDatabase =
        Room.databaseBuilder(
            context,
            CalendarDatabase::class.java,
            "calendar.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCalendarDao(db: CalendarDatabase): CalendarDao =
        db.calendarDao()
}
