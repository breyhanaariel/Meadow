package com.meadow.feature.catalog.ui.util

import com.meadow.feature.catalog.domain.model.CatalogItem
import com.meadow.feature.catalog.domain.model.CatalogType

/* ─── TITLE ─────────────────────────── */

fun CatalogItem.readTitleOrNull(): String? =
    fields.firstOrNull()?.value?.rawValue?.toString()
        ?: primaryText

fun CatalogItem.readSubtitleOrNull(): String? =
    fields
        .drop(1)
        .firstOrNull()
        ?.value
        ?.rawValue
        ?.toString()

/* ─── TYPE ─────────────────────────── */

fun CatalogItem.readType(): CatalogType =
    CatalogType.fromKey(schemaId)

/* ─── SEARCH ─────────────────────────── */

fun CatalogItem.searchBlob(): String =
    searchBlob
        ?: buildString {
            fields.forEach {
                it.value?.rawValue?.toString()?.let { v ->
                    append(v).append('\n')
                }
            }
        }
