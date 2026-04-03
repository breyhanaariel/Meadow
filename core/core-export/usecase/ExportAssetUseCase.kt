package com.meadow.core.export.usecase

import com.meadow.core.export.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import com.meadow.core.export.ImageExporter
import com.meadow.core.export.AudioExporter
import com.meadow.core.export.VideoExporter
import com.meadow.core.export.SubtitleExporter


class ExportAssetUseCase {

    suspend fun export(
        type: ExportAssetType,
        input: File,
        output: File
    ) = withContext(Dispatchers.IO) {

        when (type) {
            ExportAssetType.IMAGE ->
                ImageExporter.exportPng(input, output)

            ExportAssetType.AUDIO ->
                AudioExporter.convertToOgg(input, output)

            ExportAssetType.VIDEO ->
                VideoExporter.exportWebm(input, output)

            ExportAssetType.SUBTITLE ->
                SubtitleExporter.exportVtt(
                    subs = emptyList(),
                    outFile = output
                )

            ExportAssetType.PDF ->
                input.copyTo(output, overwrite = true)
        }
    }
}
