package com.meadow.feature.catalog.sync

import com.meadow.core.sync.api.RemoteSyncContract
import com.meadow.feature.catalog.sync.CatalogRemoteSync
import com.meadow.feature.catalog.sync.firestore.CatalogFirestoreMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CatalogSyncModule {

    @Provides
    @Singleton
    fun provideCatalogRemoteSync(
        impl: CatalogRemoteSync
    ): RemoteSyncContract = impl

    @Provides
    @Singleton
    fun provideCatalogFirestoreMapper(): CatalogFirestoreMapper =
        CatalogFirestoreMapper()
}
