package com.meadow.app.data.firebase

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class StorageHelper(
    private val storage: FirebaseStorage
) {
    suspend fun uploadFile(localPath: String, remotePath: String) {
        val ref = storage.reference.child(remotePath)
        ref.putFile(android.net.Uri.fromFile(File(localPath))).await()
    }
    suspend fun downloadFile(remotePath: String, localPath: String) {
        val file = File(localPath)
        file.parentFile?.mkdirs()
        val ref = storage.reference.child(remotePath)
        ref.getFile(file).await()
    }
}