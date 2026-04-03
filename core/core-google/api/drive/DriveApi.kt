package com.meadow.core.google.api.drive

import com.meadow.core.google.api.drive.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface DriveApi {

    @GET("drive/v3/files")
    suspend fun listFiles(
        @Query("q") query: String? = null
    ): Response<DriveFileList>

    @Multipart
    @POST("upload/drive/v3/files?uploadType=multipart")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<DriveUploadResponse>

    @POST("drive/v3/files")
    suspend fun createFolder(
        @Body body: DriveCreateFolderRequest
    ): Response<DriveFile>

    @Multipart
    @POST("upload/drive/v3/files?uploadType=multipart")
    suspend fun createFile(
        @Part metadata: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): Response<DriveFile>

    @PATCH("upload/drive/v3/files/{fileId}?uploadType=multipart")
    suspend fun updateFile(
        @Path("fileId") fileId: String,
        @Part file: MultipartBody.Part
    ): Response<DriveFile>

    @PATCH("drive/v3/files/{fileId}")
    suspend fun patchFileMetadata(
        @Path("fileId") fileId: String,
        @Body body: Map<String, Any?>
    ): Response<DriveFile>
}