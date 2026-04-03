package com.meadow.core.google.api.translate

import com.meadow.core.google.api.translate.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TranslateApi {

    @POST("language/translate/v2")
    suspend fun translate(
        @Body request: TranslateRequest
    ): Response<TranslateResponse>
}