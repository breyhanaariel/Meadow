package com.meadow.feature.project.di

import com.meadow.feature.project.data.repository.ProjectFieldHistoryRepository
import com.meadow.feature.project.domain.repository.ProjectFieldHistoryContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProjectFieldHistoryModule {

    @Binds
    @Singleton
    abstract fun bindProjectFieldHistoryRepository(
        impl: ProjectFieldHistoryRepository
    ): ProjectFieldHistoryContract
}
