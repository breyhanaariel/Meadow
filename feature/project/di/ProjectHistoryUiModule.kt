package com.meadow.feature.project.di

import com.meadow.feature.common.api.HistoryLabelResolver
import com.meadow.feature.common.api.HistoryRestoreHandler
import com.meadow.feature.project.history.ProjectHistoryLabelResolver
import com.meadow.feature.project.history.ProjectHistoryRestoreHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ProjectHistoryUiModule {

    @Binds
    @IntoSet
    abstract fun bindProjectHistoryLabelResolver(
        impl: ProjectHistoryLabelResolver
    ): HistoryLabelResolver

    @Binds
    @IntoSet
    abstract fun bindProjectHistoryRestoreHandler(
        impl: ProjectHistoryRestoreHandler
    ): HistoryRestoreHandler
}