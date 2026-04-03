package com.meadow.feature.project.ui.util

import com.meadow.core.data.fields.FieldKind
import com.meadow.core.data.fields.FieldWithValue
import com.meadow.feature.project.domain.model.Project

/* ─── TEXT READERS ─────────────────────────── */

fun Project.readTitleOrNull(): String? =
    fields.readFirstTextByKeys(
        "title",
        "project.title",
        "name"
    )

/* ─── IMAGE RESOLVER (CANONICAL) ───────────── */

fun Project.resolveCoverImageOrNull(): String? {
    val field = fields.firstOrNull {
        it.definition.kind == FieldKind.IMAGE &&
                it.definition.metadata?.get("role") == "cover"
    } ?: return null

    val raw = field.value?.rawValue ?: return null

    return when (raw) {
        is String -> raw.trim().takeIf { it.isNotBlank() }
        is List<*> -> raw.firstOrNull()
            ?.toString()
            ?.trim()
            ?.takeIf { it.isNotBlank() }
        else -> raw
            .toString()
            .trim()
            .takeIf { it.isNotBlank() }
    }
}

/* ─── INTERNAL HELPERS ───────────── */

fun List<FieldWithValue>.readFirstTextByKeys(
    vararg keys: String
): String? {
    val keySet = keys.toSet()

    val raw = firstOrNull {
        it.definition.key in keySet || it.definition.id in keySet
    }?.value?.rawValue

    return raw
        ?.toString()
        ?.trim()
        ?.takeIf { it.isNotBlank() }
}
