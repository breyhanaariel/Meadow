package com.meadow.core.google.repository

import com.meadow.core.google.api.docs.DocsApi
import com.meadow.core.google.api.docs.model.Document
import retrofit2.Response
import javax.inject.Inject

class DocsRepository @Inject constructor(
    private val api: DocsApi
) {
    suspend fun getDocument(id: String): Response<Document> =
        api.getDocument(id)
}
