package com.meadow.core.ai.domain.model

sealed class ChatScope {

    data class Project(val projectId: String) : ChatScope()
    data class Series(val seriesId: String) : ChatScope()

    data class Feature(
        val owner: ChatScope,
        val feature: FeatureType
    ) : ChatScope()

    data class Field(
        val owner: ChatScope,
        val feature: FeatureType,
        val fieldKey: String
    ) : ChatScope()
}

enum class FeatureType {
    PROJECT,
    CATALOG,
    SCRIPT,
    PLOT_CARDS,
    STORYBOARD,
    TIMELINE,
    WIKI,
    MINDMAP,
    FAMILY_TREE
}
