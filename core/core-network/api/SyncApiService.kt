package com.meadow.core.network.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SyncApiService {
    @GET("sync/{collection}")
    suspend fun downloadCollection(
        @Path("collection") collection: String
    ): List<Map<String, Any>>

    @POST("sync/{collection}")
    suspend fun uploadCollection(
        @Path("collection") collection: String,
        @Body payload: List<Map<String, Any>>
    )
}
