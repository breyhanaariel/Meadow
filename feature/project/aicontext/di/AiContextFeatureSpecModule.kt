package com.meadow.feature.project.aicontext.di

import com.meadow.feature.aicontext.domain.AiContextFeatureCountProvider
import com.meadow.feature.aicontext.domain.AiContextFeatureSpec
import com.meadow.feature.project.domain.model.ProjectFeatureCountProvider
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AiContextFeatureSpecBinding {

    @Binds
    @IntoSet
    abstract fun bindAiContextSpec(
        spec: AiContextFeatureSpec
    ): ProjectFeatureSpec
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AiContextFeatureCountModule {

    @Binds
    @IntoSet
    abstract fun bindAiContextCountProvider(
        impl: AiContextFeatureCountProvider
    ): ProjectFeatureCountProvider
}
