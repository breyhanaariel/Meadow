package com.meadow.feature.catalog.sync.sheets

import com.meadow.core.google.api.drive.internal.DriveApiClient
import com.meadow.core.google.api.drive.internal.DriveFolderResolver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogSheetsDriveResolver @Inject constructor(
    private val driveApiClient: DriveApiClient,
    private val folderResolver: DriveFolderResolver
) {

    companion object {
        private const val ROOT_FOLDER = "Meadow"
        private const val SPREADSHEET_NAME = "Catalog"
    }

    private var cachedSpreadsheetId: String? = null

    suspend fun getOrCreateMeadowFolder(): String =
        folderResolver.getOrCreateFolder(ROOT_FOLDER)

    /**
     * Creates (or reuses) ONE spreadsheet called "Catalog"
     * inside the Meadow folder.
     */
    suspend fun getOrCreateCatalogSpreadsheet(
        meadowFolderId: String
    ): String {
        if (cachedSpreadsheetId != null) {
            return cachedSpreadsheetId!!
        }

        val spreadsheetId =
            driveApiClient.getOrCreateSpreadsheet(
                name = SPREADSHEET_NAME,
                parentFolderId = meadowFolderId
            )

        cachedSpreadsheetId = spreadsheetId
        return spreadsheetId
    }
}
