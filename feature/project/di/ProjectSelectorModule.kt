package com.meadow.feature.project.di

import com.meadow.feature.project.api.ProjectSelector
import com.meadow.feature.project.api.ProjectSelectorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProjectSelectorModule {

    @Binds
    @Singleton
    abstract fun bindProjectSelector(
        impl: ProjectSelectorImpl
    ): ProjectSelector
}
