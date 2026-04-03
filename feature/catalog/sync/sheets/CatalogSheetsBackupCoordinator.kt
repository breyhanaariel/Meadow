package com.meadow.feature.catalog.sync.sheets

import com.meadow.core.google.engine.GoogleAuthManager
import com.meadow.feature.catalog.domain.repository.CatalogRepositoryContract
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogSheetsBackupCoordinator @Inject constructor(
    private val repo: CatalogRepositoryContract,
    private val driveResolver: CatalogSheetsDriveResolver,
    private val sheetsRepo: CatalogSheetsRepository,
    private val batchExporter: CatalogSheetsBatchExporter,
    private val googleAuthManager: GoogleAuthManager
) {

    private val _events = MutableSharedFlow<CatalogSheetsUiEvent>()
    val events = _events.asSharedFlow()

    suspend fun backupSingle(catalogItemId: String) {
        if (!googleAuthManager.isSignedIn()) {
            throw IllegalStateException("Google account not connected")
        }

        _events.emit(CatalogSheetsUiEvent.Progress("Preparing spreadsheet…"))

        val meadowFolder = driveResolver.getOrCreateMeadowFolder()
        val spreadsheetId =
            driveResolver.getOrCreateCatalogSpreadsheet(meadowFolder)

        val item = repo.getCatalogById(catalogItemId)
            ?: return

        _events.emit(CatalogSheetsUiEvent.Progress("Backing up catalog item…"))

        batchExporter.exportBatch(
            spreadsheetId,
            listOf(item)
        )

        repo.updateCatalogSyncMeta(
            item.id,
            item.syncMeta.copy(
                lastDriveBackupAt = System.currentTimeMillis()
            )
        )

        _events.emit(
            CatalogSheetsUiEvent.Success("Catalog item backed up")
        )
    }

    suspend fun backupAllForScope(
        projectId: String?,
        seriesId: String?
    ) {
        if (!googleAuthManager.isSignedIn()) {
            throw IllegalStateException("Google account not connected")
        }

        _events.emit(CatalogSheetsUiEvent.Progress("Preparing spreadsheet…"))

        val meadowFolder = driveResolver.getOrCreateMeadowFolder()
        val spreadsheetId =
            driveResolver.getOrCreateCatalogSpreadsheet(meadowFolder)

        val items = repo.getAllCatalogsOnce()
            .filter {
                (projectId != null && it.projectId == projectId) ||
                        (seriesId != null && it.seriesId == seriesId)
            }

        if (items.isEmpty()) return

        _events.emit(
            CatalogSheetsUiEvent.Progress("Exporting ${items.size} items…")
        )

        batchExporter.exportBatch(spreadsheetId, items)
        val now = System.currentTimeMillis()
        items.forEach { item ->
            repo.updateCatalogSyncMeta(
                item.id,
                item.syncMeta.copy(
                    lastDriveBackupAt = now
                )
            )
        }

        _events.emit(
            CatalogSheetsUiEvent.Success("Catalog export complete")
        )
    }
}
