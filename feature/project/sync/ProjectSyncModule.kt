package com.meadow.feature.project.sync.di

import com.meadow.core.sync.api.RemoteSyncContract
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.sync.ProjectRemoteSync
import com.meadow.feature.project.sync.drive.ProjectDriveBackupCoordinator
import com.meadow.feature.project.sync.firestore.ProjectFirestoreMapper
import com.meadow.feature.project.sync.work.ProjectSyncScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProjectSyncModule {

    @Provides
    @Singleton
    fun provideProjectRemoteSync(
        impl: ProjectRemoteSync
    ): RemoteSyncContract = impl

    @Provides
    @Singleton
    fun provideProjectFirestoreMapper(): ProjectFirestoreMapper =
        ProjectFirestoreMapper()

}