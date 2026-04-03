package com.meadow.core.sync.remote

import com.google.firebase.firestore.DocumentSnapshot


interface FirestoreSyncMapper<T> {
    fun fromDocument(doc: DocumentSnapshot): T?
    fun toDocument(item: T): FirestoreDocument
}

data class FirestoreDocument(
    val id: String,
    val data: Map<String, Any?>
)
