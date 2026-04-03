package com.meadow.core.export.usecase

import com.meadow.core.data.fields.FieldKind

fun FieldKind.toExportAssetType(): ExportAssetType? =
    when (this) {
        FieldKind.IMAGE -> ExportAssetType.IMAGE
        FieldKind.AUDIO -> ExportAssetType.AUDIO
        FieldKind.VIDEO -> ExportAssetType.VIDEO
        FieldKind.SUBTITLE -> ExportAssetType.SUBTITLE
        FieldKind.PDF -> ExportAssetType.PDF,
        FieldKind.FILE -> ExportAssetType.FILE
        else -> null
    }
