package com.meadow.app.di

import com.meadow.app.data.repository.*
import com.meadow.app.data.room.dao.*
import com.meadow.app.data.firebase.FirestoreHelper
import com.meadow.app.data.firebase.StorageHelper
import com.meadow.app.data.firebase.DriveSyncHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * RepositoryModule.kt
 *
 * Provides all repository implementations to ViewModels.
 * Each repository handles data logic for a single domain.
 */

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /** Project data (CRUD + sync logic). */
    @Provides
    @Singleton
    fun provideProjectRepository(dao: ProjectDao): ProjectRepository = ProjectRepository(dao)

    /** Catalog repository handles all catalog item data. */
    @Provides
    @Singleton
    fun provideCatalogRepository(dao: CatalogDao): CatalogRepository = CatalogRepository(dao)

    /** Script repository manages script data and versions. */
    @Provides
    @Singleton
    fun provideScriptRepository(dao: ScriptDao): ScriptRepository = ScriptRepository(dao)

    /** Optional: when Firebase helpers exist, repositories can be extended with them. */
    @Provides
    @Singleton
    fun provideSyncRepository(
        firestore: FirestoreHelper,
        storage: StorageHelper,
        drive: DriveSyncHelper
    ): SyncRepository = SyncRepository(firestore, storage, drive)
}
