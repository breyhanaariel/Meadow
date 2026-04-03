package com.meadow.core.data.firebase

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSyncManager @Inject constructor(
    private val firestoreHelper: FirestoreHelper,
    private val storageHelper: StorageHelper
) {

    suspend fun syncStorageOnly(): Result<Unit> =
        runCatching { storageHelper.syncAssets() }
}
