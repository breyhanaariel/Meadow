package com.meadow.core.sync.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.meadow.core.sync.api.SyncableDataSource
import kotlinx.coroutines.tasks.await

class FirestoreSyncDataSource<T>(
    private val firestore: FirebaseFirestore,
    private val collectionPath: String,
    private val mapper: FirestoreSyncMapper<T>
) : SyncableDataSource<T> {

    override suspend fun getRemoteData(): List<T> {
        val snapshot = firestore.collection(collectionPath).get().await()
        return snapshot.documents.mapNotNull { mapper.fromDocument(it) }
    }

    override suspend fun getLocalData(): List<T> {
        throw IllegalStateException("Remote source cannot fetch local data")
    }

    override suspend fun pushLocalData(data: List<T>) {
        val col = firestore.collection(collectionPath)
        data.forEach { item ->
            val doc = mapper.toDocument(item)
            col.document(doc.id).set(doc.data).await()
        }
    }

    override suspend fun saveRemoteData(data: List<T>) {
        pushLocalData(data)
    }

    override suspend fun merge(
        localData: List<T>,
        remoteData: List<T>
    ): List<T> {
        // Default strategy: remote wins
        return remoteData
    }
}
