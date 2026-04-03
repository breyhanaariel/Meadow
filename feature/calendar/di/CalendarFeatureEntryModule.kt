package com.meadow.feature.calendar.di

import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.calendar.api.CalendarFeatureEntryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet


@Module
@InstallIn(SingletonComponent::class)
abstract class CalendarFeatureEntryModule {

    @Binds
    @IntoSet
    abstract fun bindCalendarFeatureEntry(
        impl: CalendarFeatureEntryImpl
    ): FeatureEntry
}
