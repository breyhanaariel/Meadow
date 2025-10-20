package com.meadow.app.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.meadow.app.data.firebase.FirestoreHelper
import com.meadow.app.data.firebase.StorageHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * FirebaseModule.kt
 *
 * Provides Firebase dependencies for Firestore + Storage.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideFirestoreHelper(db: FirebaseFirestore): FirestoreHelper = FirestoreHelper(db)

    @Provides
    @Singleton
    fun provideStorageHelper(storage: FirebaseStorage): StorageHelper = StorageHelper(storage)
}