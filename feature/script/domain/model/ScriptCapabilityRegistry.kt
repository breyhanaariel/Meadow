package com.meadow.feature.script.domain.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScriptCapabilityRegistry @Inject constructor() {

    fun capabilitiesFor(
        type: ScriptType
    ): ScriptCapabilities {
        return when (type) {
            ScriptType.COMIC -> StaticCapabilities(
                maxScripts = 1,
                supportsSeasons = false,
                allowsPageImages = true,
                allowsInlineImages = false,
                allowsVideo = false,
                allowsSubtitles = false,
                allowsApk = false,
                exportFormats = setOf(ExportFormat.CBZ, ExportFormat.FOUNTAIN)
            )
            ScriptType.NOVEL -> StaticCapabilities(
                maxScripts = 1,
                supportsSeasons = false,
                allowsPageImages = false,
                allowsInlineImages = true,
                allowsVideo = false,
                allowsSubtitles = false,
                allowsApk = false,
                exportFormats = setOf(ExportFormat.PDF, ExportFormat.EPUB, ExportFormat.FOUNTAIN)
            )
            ScriptType.TV -> StaticCapabilities(
                maxScripts = null,
                supportsSeasons = true,
                allowsPageImages = false,
                allowsInlineImages = true,
                allowsVideo = true,
                allowsSubtitles = true,
                allowsApk = false,
                exportFormats = setOf(ExportFormat.FOUNTAIN, ExportFormat.SRT, ExportFormat.VTT)
            )
            ScriptType.MOVIE -> StaticCapabilities(
                maxScripts = 1,
                supportsSeasons = false,
                allowsPageImages = false,
                allowsInlineImages = true,
                allowsVideo = true,
                allowsSubtitles = true,
                allowsApk = false,
                exportFormats = setOf(ExportFormat.FOUNTAIN, ExportFormat.SRT, ExportFormat.VTT)
            )
            ScriptType.GAME -> StaticCapabilities(
                maxScripts = 1,
                supportsSeasons = false,
                allowsPageImages = false,
                allowsInlineImages = true,
                allowsVideo = false,
                allowsSubtitles = false,
                allowsApk = true,
                exportFormats = setOf(ExportFormat.RENPY, ExportFormat.ZIP)
            )
        }
    }

    private data class StaticCapabilities(
        override val maxScripts: Int?,
        override val supportsSeasons: Boolean,
        override val allowsPageImages: Boolean,
        override val allowsInlineImages: Boolean,
        override val allowsVideo: Boolean,
        override val allowsSubtitles: Boolean,
        override val allowsApk: Boolean,
        override val exportFormats: Set<ExportFormat>
    ) : ScriptCapabilities
}
