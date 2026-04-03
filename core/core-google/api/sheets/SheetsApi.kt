package com.meadow.core.google.api.sheets

import com.meadow.core.google.api.sheets.model.*
import retrofit2.Response
import retrofit2.http.*

interface SheetsApi {

    @GET("v4/spreadsheets/{spreadsheetId}/values/{range}")
    suspend fun readRange(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String
    ): Response<ValueRange>

    @PUT("v4/spreadsheets/{spreadsheetId}/values/{range}?valueInputOption=RAW")
    suspend fun writeRange(
        @Path("spreadsheetId") spreadsheetId: String,
        @Path("range") range: String,
        @Body body: ValueRange
    ): Response<SheetsUpdateResponse>
}