package com.meadow.core.common.constants

object AppConstants {

    /* ─── NETWORK ───────────────────── */
    const val NETWORK_TIMEOUT_MS: Long = 30_000
    const val IO_TIMEOUT_MS: Long = 60_000

    /* ─── PAGINATION ────────────────── */
    const val PAGE_SIZE_DEFAULT = 25
    const val PAGE_SIZE_MAX = 200

    /* ─── DATE FORMATS ──────────────── */
    const val ISO_INSTANT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val ISO_LOCAL_DATE = "yyyy-MM-dd"
    const val ISO_LOCAL_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss"

    /* ─── FILE SYSTEM ───────────────── */
    const val EXPORT_FOLDER_NAME = "meadow_exports"
    const val BACKUP_FILE_SUFFIX = ".meadow.backup.json"
}