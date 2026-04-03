package com.meadow.core.google.repository

import com.meadow.core.google.api.sheets.SheetsApi
import com.meadow.core.google.api.sheets.model.*
import retrofit2.Response
import javax.inject.Inject

class SheetsRepository @Inject constructor(
    private val api: SheetsApi
) {
    suspend fun read(
        sheetId: String,
        range: String
    ): Response<ValueRange> =
        api.readRange(sheetId, range)

    suspend fun write(
        sheetId: String,
        range: String,
        values: ValueRange
    ): Response<SheetsUpdateResponse> =
        api.writeRange(sheetId, range, values)
}
