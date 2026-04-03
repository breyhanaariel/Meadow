package com.meadow.feature.project.di

import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.project.data.repository.ProjectRepository
import com.meadow.feature.project.data.repository.SeriesRepository
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import com.meadow.feature.project.api.ProjectFeatureEntryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProjectModule {

    @Binds
    @Singleton
    abstract fun bindSeriesRepository(
        impl: SeriesRepository
    ): SeriesRepositoryContract

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        impl: ProjectRepository
    ): ProjectRepositoryContract

    @Binds
    @Singleton
    abstract fun bindProjectFeatureEntry(
        impl: ProjectFeatureEntryImpl
    ): FeatureEntry

}