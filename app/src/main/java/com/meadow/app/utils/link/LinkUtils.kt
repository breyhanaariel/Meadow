package com.meadow.app.utils.linl

import com.meadow.app.data.model.LinkReference

/**
 * LinkUtils.kt
 *
 * Converts @mentions or special markup in scripts
 * into clickable link references that connect to catalog items.
 */
object LinkUtils {

    private val linkRegex = Regex("@\\[(.*?)\\]\\((.*?)\\)")

    /**
     * Parses links like @[Character Name](character:123)
     * into LinkReference objects.
     */
    fun extractLinks(text: String): List<LinkReference> {
        return linkRegex.findAll(text).map {
            val label = it.groupValues[1]
            val ref = it.groupValues[2]
            val (type, id) = ref.split(":")
            LinkReference(label, type, id)
        }.toList()
    }

    /**
     * Replaces links with plain text for display in Fountain scripts.
     */
    fun stripLinks(text: String): String {
        return text.replace(linkRegex) { matchResult ->
            matchResult.groupValues[1]
        }
    }

    /**
     * Highlights linked text for editing previews.
     */
    fun highlightLinks(text: String): String {
        return text.replace(linkRegex) {
            "**${it.groupValues[1]}**"
        }
    }
}
