package com.meadow.core.google.repository

import com.meadow.core.google.api.drive.DriveApi
import com.meadow.core.google.api.drive.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class DriveRepository @Inject constructor(
    private val api: DriveApi
) {
    suspend fun listFiles(query: String?): Response<DriveFileList> =
        api.listFiles(query)

    suspend fun uploadFile(
        file: MultipartBody.Part
    ): Response<DriveUploadResponse> =
        api.uploadFile(file)
}
