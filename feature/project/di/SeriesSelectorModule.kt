package com.meadow.feature.project.di

import com.meadow.feature.project.api.SeriesSelector
import com.meadow.feature.project.api.SeriesSelectorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SelectorModule {

    @Binds
    @Singleton
    abstract fun bindSeriesSelector(
        impl: SeriesSelectorImpl
    ): SeriesSelector
}
