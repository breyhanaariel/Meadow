package com.meadow.feature.catalog.di

import com.meadow.feature.catalog.domain.CatalogFeatureCountProvider
import com.meadow.feature.catalog.domain.CatalogFeatureSpec
import com.meadow.feature.project.domain.model.ProjectFeatureCountProvider
import com.meadow.feature.project.domain.model.ProjectFeatureSpec
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogFeatureSpecBinding {

    @Binds
    @IntoSet
    abstract fun bindCatalogSpec(
        spec: CatalogFeatureSpec
    ): ProjectFeatureSpec
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogFeatureCountModule {

    @Binds
    @IntoSet
    abstract fun bindCatalogCountProvider(
        impl: CatalogFeatureCountProvider
    ): ProjectFeatureCountProvider
}