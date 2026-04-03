package com.meadow.feature.project.di

import com.meadow.feature.project.domain.registry.ProjectTemplateRegistry
import com.meadow.feature.project.internal.template.AssetProjectTemplateRegistry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ProjectTemplateModule {

    @Provides
    @Singleton
    fun provideProjectTemplateRegistry(
        impl: AssetProjectTemplateRegistry
    ): ProjectTemplateRegistry = impl
}