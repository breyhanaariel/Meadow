package com.meadow.core.sync.di

import com.google.firebase.firestore.FirebaseFirestore
import com.meadow.core.sync.api.RemoteSyncContract
import com.meadow.core.sync.api.SyncableDataSource
import com.meadow.core.sync.engine.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideSerializer(): SyncSerializer<Any> =
        DefaultSyncSerializer()

    @Provides
    @Singleton
    fun provideHasher(
        serializer: SyncSerializer<Any>
    ): SyncHasher<Any> = SyncHasher(serializer)

    @Provides
    @Singleton
    fun provideResolver(): SyncConflictResolver<Any> =
        DefaultConflictResolver()

    @Provides
    @Singleton
    fun provideSyncEngine(
        dataSources: Set<@JvmSuppressWildcards SyncableDataSource<Any>>,
        serializer: SyncSerializer<Any>,
        hasher: SyncHasher<Any>,
        resolver: SyncConflictResolver<Any>
    ): RemoteSyncContract {
        val first = dataSources.firstOrNull()
            ?: throw IllegalStateException("No SyncableDataSource was bound!")

        return SyncEngine(
            dataSource = first,
            serializer = serializer,
            hasher = hasher,
            conflictResolver = resolver
        )
    }
}
