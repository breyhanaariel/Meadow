package com.meadow.feature.project.di

import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
abstract class ProjectFeatureSpecModule {

    @Multibinds
    abstract fun bindProjectFeatureSpecs(): Set<ProjectFeatureSpec>
}
