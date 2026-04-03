package com.meadow.core.google.repository

import com.meadow.core.google.api.translate.TranslateApi
import com.meadow.core.google.api.translate.model.*
import retrofit2.Response
import javax.inject.Inject

class TranslateRepository @Inject constructor(
    private val api: TranslateApi
) {
    suspend fun translate(
        text: String,
        target: String
    ): Response<TranslateResponse> =
        api.translate(TranslateRequest(text, target))
}
