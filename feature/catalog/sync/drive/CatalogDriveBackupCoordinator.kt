package com.meadow.feature.catalog.sync.drive

import com.meadow.core.google.api.drive.internal.DriveBackupRepository
import com.meadow.core.google.api.drive.internal.DriveUploadResult
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogDriveBackupCoordinator @Inject constructor(
    private val driveRepository: DriveBackupRepository,
    private val catalogRepo: CatalogRepositoryContract,
    private val serializer: CatalogDriveSerializer
) {

    private fun catalogAppProps(schemaType: String, catalogId: String): Map<String, String> =
        mapOf(
            "schemaType" to schemaType,
            "catalogId" to catalogId
        )

    suspend fun backupCatalog(catalogId: String) {
        val catalog = catalogRepo.getCatalogById(catalogId) ?: return

        val result = driveRepository.backupFile(
            fileName = CatalogDriveFileNamer.fileName(catalog),
            json = serializer.serialize(catalog),
            appProperties = catalogAppProps(
                schemaType = catalog.schemaId,
                catalogId = catalog.id
            )
        )

        if (result is DriveUploadResult.Failure) {
            catalogRepo.markSyncFailure(listOf(catalogId), result.reason)
            throw result.throwable ?: RuntimeException(result.reason)
        }
    }

    suspend fun backupAllNow() {
        catalogRepo.getAllCatalogsOnce().forEach { catalog ->
            val result = driveRepository.backupFile(
                fileName = CatalogDriveFileNamer.fileName(catalog),
                json = serializer.serialize(catalog),
                appProperties = catalogAppProps(
                    schemaType = catalog.schemaId,
                    catalogId = catalog.id
                )
            )

            if (result is DriveUploadResult.Failure) {
                catalogRepo.markSyncFailure(listOf(catalog.id), result.reason)
            }
        }
    }
}
