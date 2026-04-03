package com.meadow.core.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreHelper @Inject constructor(
    val firestore: FirebaseFirestore
) {

    suspend fun upload(
        collection: String,
        documentId: String,
        data: Any
    ) {
        firestore.collection(collection)
            .document(documentId)
            .set(data, SetOptions.merge())
            .await()
    }

    suspend inline fun <reified T> get(
        collection: String,
        documentId: String
    ): T? {
        val snap = firestore.collection(collection)
            .document(documentId)
            .get()
            .await()

        return snap.toObject(T::class.java)
    }
    suspend inline fun <reified T> query(
        collection: String,
        field: String,
        value: Any
    ): List<T> {
        val snap = firestore.collection(collection)
            .whereEqualTo(field, value)
            .get()
            .await()

        return snap.toObjects(T::class.java)
    }

    suspend inline fun <reified T> getAll(
        collection: String
    ): List<T> {
        val snap = firestore.collection(collection)
            .get()
            .await()

        return snap.toObjects(T::class.java)
    }
}
