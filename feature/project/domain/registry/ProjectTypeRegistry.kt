package com.meadow.feature.project.domain.registry

import com.meadow.feature.project.domain.model.ProjectType

object ProjectTypeRegistry {

    data class TypeConfig(
        val type: ProjectType,
        val colorHex: String,
        val allowedFeatures: List<String>,
        val templateKey: String
    )

    private val registry = mapOf(
        ProjectType.COMIC to TypeConfig(
            type = ProjectType.COMIC,
            colorHex = "#F4A6C1",
            allowedFeatures = listOf("script", "catalog", "calendar", "plot_cards", "timeline", "mindmap", "wiki", "family_tree"),
            templateKey = "comic"
        ),
        ProjectType.NOVEL to TypeConfig(
            type = ProjectType.NOVEL,
            colorHex = "#B39DDB",
            allowedFeatures = listOf("script", "catalog", "calendar", "plot_cards", "timeline", "mindmap", "wiki", "family_tree"),
            templateKey = "novel"
        ),
        ProjectType.TV_SHOW to TypeConfig(
            type = ProjectType.TV_SHOW,
            colorHex = "#90CAF9",
            allowedFeatures = listOf("script", "catalog", "calendar", "plot_cards", "storyboard", "shot_list", "timeline", "mindmap", "wiki", "family_tree"),
            templateKey = "tv_show"
        ),
        ProjectType.MOVIE to TypeConfig(
            type = ProjectType.MOVIE,
            colorHex = "#A5D6A7",
            allowedFeatures = listOf("script", "catalog", "calendar", "plot_cards", "storyboard", "shot_list", "timeline", "mindmap", "wiki", "family_tree"),
            templateKey = "movie"
        ),
        ProjectType.GAME to TypeConfig(
            type = ProjectType.GAME,
            colorHex = "#FFCCBC",
            allowedFeatures = listOf("script", "catalog", "calendar", "plot_cards", "timeline", "mindmap", "wiki", "family_tree"),
            templateKey = "game"
        )
    )

    fun get(type: ProjectType): TypeConfig =
        registry[type] ?: registry.getValue(ProjectType.NOVEL)
}
