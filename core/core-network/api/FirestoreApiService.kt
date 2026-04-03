package com.meadow.core.network.api

import retrofit2.http.GET
import retrofit2.http.Path


interface FirestoreApiService {
    @GET("firestore/{collection}/{id}")
    suspend fun getDocument(
        @Path("collection") collection: String,
        @Path("id") id: String
    ): Map<String, Any>
}
