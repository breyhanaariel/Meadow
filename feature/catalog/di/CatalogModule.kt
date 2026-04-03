package com.meadow.feature.catalog.di

import android.content.Context
import androidx.room.Room
import com.meadow.feature.catalog.data.local.CatalogDatabase
import com.meadow.feature.catalog.data.local.CatalogFieldValueDao
import com.meadow.feature.catalog.data.local.CatalogItemDao
import com.meadow.feature.catalog.data.local.CatalogLinkDao
import com.meadow.feature.catalog.data.repository.CatalogRepository
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogModule {

    @Binds
    abstract fun bindCatalogRepository(
        repository: CatalogRepository
    ): CatalogRepositoryContract

    companion object {

        @Provides
        @Singleton
        fun provideCatalogDatabase(
            @ApplicationContext context: Context
        ): CatalogDatabase =
            Room.databaseBuilder(
                context,
                CatalogDatabase::class.java,
                "catalog.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        @Provides
        fun provideCatalogItemDao(
            database: CatalogDatabase
        ): CatalogItemDao =
            database.catalogItemDao()

        @Provides
        fun provideCatalogLinkDao(
            database: CatalogDatabase
        ): CatalogLinkDao =
            database.catalogLinkDao()

        @Provides
        fun provideCatalogFieldValueDao(
            database: CatalogDatabase
        ): CatalogFieldValueDao =
            database.catalogFieldValueDao()
    }
}
