package com.meadow.feature.project.di

import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.project.api.ProjectFeatureEntryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ProjectFeatureEntryModule {

    @Binds
    @IntoSet
    abstract fun bindProjectFeatureEntry(
        impl: ProjectFeatureEntryImpl
    ): FeatureEntry
}

