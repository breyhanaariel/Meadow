package com.meadow.core.google.api.docs

import com.meadow.core.google.api.docs.model.Document
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DocsApi {

    @GET("docs/v1/documents/{documentId}")
    suspend fun getDocument(
        @Path("documentId") id: String
    ): Response<Document>
}