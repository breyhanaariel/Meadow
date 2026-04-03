package com.meadow.core.export

import java.io.File

object ExportManager {
    enum class ExportType { PDF, EPUB, CBZ, WEBM, WEBP, OGG, VTT }

    suspend fun export(type: ExportType, input: Any, outFile: File): Boolean = when (type) {
        ExportType.PDF -> {
            if (input is String) PdfExporter.textToPdf(input, outFile)
            true
        }
        ExportType.EPUB -> {
            if (input is List<*>) {
                val chapters = input.filterIsInstance<String>()
                EpubExporter.exportSimple("Untitled", "Anonymous", chapters, outFile)
            }
            true
        }
        ExportType.CBZ -> {
            if (input is File) ArchiveExport.exportCbz(input, outFile)
            true
        }
        ExportType.WEBM -> {
            if (input is File) VideoExporter.exportWebm(input, outFile)
            true
        }
        ExportType.WEBP -> {
            if (input is File) ImageExporter.convertToWebp(input, outFile)
            true
        }
        ExportType.OGG -> {
            if (input is File) AudioExporter.convertToOgg(input, outFile)
            true
        }
        ExportType.VTT -> {
            if (input is List<*>) {
                val subs = input.filterIsInstance<Pair<Double, String>>()
                SubtitleExporter.exportVtt(subs, outFile)
            }
            true
        }
    }
}