package com.meadow.app.core.constants

/**
 * ProjectTypes.kt
 *
 * This file defines all valid project types (Book, Movie, Game, etc.)
 * It’s implemented as a sealed class to make it easy to extend later.
 */

sealed class ProjectType(val id: String, val label: String) {
    object Novel : ProjectType("NOVEL", "Novel / Book")
    object Movie : ProjectType("MOVIE", "Movie / Film")
    object TVShow : ProjectType("TV_SHOW", "TV Show / Series")
    object Play : ProjectType("PLAY", "Theatrical Play")
    object Comic : ProjectType("COMIC", "Comic Book / Manga")
    object Game : ProjectType("GAME", "Game Project")
    object Anime : ProjectType("ANIME", "Anime Project")

    companion object {
        // Provides a list for dropdowns or templates
        fun allTypes() = listOf(Novel, Movie, TVShow, Play, Comic, Game, Anime)
    }
}
