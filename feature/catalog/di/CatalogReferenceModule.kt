package com.meadow.feature.catalog.di

import com.meadow.core.ui.state.ReferenceDataProvider
import com.meadow.feature.catalog.api.CatalogReferenceDataProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogReferenceModule {

    @Binds
    abstract fun bindReferenceDataProvider(
        impl: CatalogReferenceDataProvider
    ): ReferenceDataProvider
}
