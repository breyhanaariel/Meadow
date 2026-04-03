package com.meadow.feature.project.aicontext.di

import com.meadow.feature.project.aicontext.api.AiContextFeatureEntryImpl
import com.meadow.feature.common.ui.navigation.FeatureEntry
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AiContextFeatureEntryModule {

    @Binds
    @IntoSet
    abstract fun bindAiContextFeatureEntry(
        impl: AiContextFeatureEntryImpl
    ): FeatureEntry
}