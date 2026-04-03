package com.meadow.feature.catalog.di

import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.catalog.api.CatalogFeatureEntryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet


@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogFeatureEntryModule {

    @Binds
    @IntoSet
    abstract fun bindCatalogFeatureEntry(
        impl: CatalogFeatureEntryImpl
    ): FeatureEntry
}
