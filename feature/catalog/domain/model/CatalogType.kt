package com.meadow.feature.catalog.domain.model

enum class CatalogType(
    val key: String
) {
    /* ─── ALL PROJECT TYPES ───────────────────── */
    ANIMAL("animal"),
    CHARACTER("character"),
    LOCATION("location"),
    ORGANIZATION("organization"),
    PROP("prop"),
    VEHICLE("vehicle"),
    WARDROBE("wardrobe"),
    STYLE_GUIDE("style_guide"),


    /* ─── COMIC ───────────────────── */

    COLOR_GUIDE("color_guide"),

    /* ─── GAME ───────────────────── */

    INVENTORY_ITEM("inventory_item"),
    SKILL("skill"),

    /* ─── NOVEL ───────────────────── */


    /* ─── TV SHOW + MOVIE ───────────────────── */

    ACTOR("actor"),
    CAST_MEMBER("cast_member"),
    HAIR_FX("hair_fx"),
    MAKEUP_FX("makeup_fx"),
    SET("set"),
    SOUND_FX("sound_fx"),
    STUNT_FX("stunt_fx"),
    VISUAL_FX("visual_fx"),

    /* ─── FALLBACK ───────────────────── */

    UNKNOWN("unknown");

    companion object {
        fun fromKey(key: String?): CatalogType =
            entries.firstOrNull { it.key == key } ?: UNKNOWN
    }
}
