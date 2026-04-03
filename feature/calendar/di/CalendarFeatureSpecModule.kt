package com.meadow.feature.calendar.di

import com.meadow.feature.calendar.domain.CalendarFeatureCountProvider
import com.meadow.feature.calendar.domain.CalendarFeatureSpec
import com.meadow.feature.project.domain.model.ProjectFeatureCountProvider
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class CalendarFeatureSpecBinding {

    @Binds
    @IntoSet
    abstract fun bindCalendarSpec(
        spec: CalendarFeatureSpec
    ): ProjectFeatureSpec
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CalendarFeatureCountModule {

    @Binds
    @IntoSet
    abstract fun bindCalendarCountProvider(
        impl: CalendarFeatureCountProvider
    ): ProjectFeatureCountProvider
}