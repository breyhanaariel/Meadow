package com.meadow.app.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.meadow.app.data.firebase.FirestoreHelper
import com.meadow.app.data.firebase.StorageHelper
import com.meadow.app.data.firebase.DriveSyncHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * SyncModule.kt
 *
 * Provides Firebase and WorkManager dependencies for syncing and background jobs.
 */

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    /** Firebase Firestore instance for structured data. */
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    /** Firebase Storage for large assets like images and audio. */
    @Provides
    @Singleton
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    /** Helper classes that wrap Firebase operations. */
    @Provides @Singleton fun provideFirestoreHelper(db: FirebaseFirestore) = FirestoreHelper(db)
    @Provides @Singleton fun provideStorageHelper(storage: FirebaseStorage) = StorageHelper(storage)
    @Provides @Singleton fun provideDriveHelper() = DriveSyncHelper()

    /** Provides WorkManager for scheduling sync jobs. */
    @Provides
    @Singleton
    fun provideWorkManager(context: Context): WorkManager = WorkManager.getInstance(context)
}
