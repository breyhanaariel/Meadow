package com.meadow.core.google.api.drive.di

import com.meadow.core.google.api.drive.DriveBackupContract
import com.meadow.core.google.api.drive.DriveBackupManager
import com.meadow.core.google.api.drive.internal.BackupDataSource
import com.meadow.core.google.api.drive.internal.DriveApiClient
import com.meadow.core.google.api.drive.internal.DriveApiClientImpl
import com.meadow.core.google.api.drive.internal.DriveBackupRepository
import com.meadow.core.google.api.drive.internal.DriveBackupSerializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DriveBackupModule {

    @Binds
    @Singleton
    abstract fun bindDriveBackupContract(
        impl: DriveBackupManager
    ): DriveBackupContract

    @Binds
    @Singleton
    abstract fun bindDriveApiClient(
        impl: DriveApiClientImpl
    ): DriveApiClient
}


@Module
@InstallIn(SingletonComponent::class)
object DriveBackupProvidesModule {

    @Provides
    fun provideBackupDataSources(): List<BackupDataSource> =
        emptyList()

    @Provides
    @Singleton
    fun provideDriveBackupRepository(
        api: DriveApiClient,
        serializer: DriveBackupSerializer
    ): DriveBackupRepository =
        DriveBackupRepository(api,  serializer)
}
