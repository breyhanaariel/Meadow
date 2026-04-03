package com.meadow.feature.script.domain.model

interface ScriptCapabilities {
    val maxScripts: Int?
    val supportsSeasons: Boolean
    val allowsPageImages: Boolean
    val allowsInlineImages: Boolean
    val allowsVideo: Boolean
    val allowsSubtitles: Boolean
    val allowsApk: Boolean
    val exportFormats: Set<ExportFormat>
}
