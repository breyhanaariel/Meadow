package com.meadow.feature.catalog.internal.schema

import androidx.annotation.DrawableRes
import com.meadow.feature.catalog.R

internal data class CatalogSchemaDefinition(
    val schemaId: String,
    val labelKey: String,
    @DrawableRes val iconResId: Int,
    val assetPath: String,
    val primaryFieldKey: String,
    val appliesToProjectTypeKeys: Set<String>
)

internal object CatalogSchemaDefinitions {


    private val ALL_PROJECT_TYPES = setOf("base", "novel", "game", "comic", "tvshow", "movie")

    val ALL: List<CatalogSchemaDefinition> = listOf(

        /* ─── COMMON (ALL PROJECT TYPES) ───────────────────── */

        CatalogSchemaDefinition(
            schemaId = "character",
            labelKey = "schema_character",
            iconResId = R.drawable.ic_character,
            assetPath = "catalog_templates/common/character.json",
            primaryFieldKey = "full_name",
            appliesToProjectTypeKeys = ALL_PROJECT_TYPES
        ),
        CatalogSchemaDefinition(
            schemaId = "location",
            labelKey = "schema_location",
            iconResId = R.drawable.ic_location,
            assetPath = "catalog_templates/common/location.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = ALL_PROJECT_TYPES
        ),
        CatalogSchemaDefinition(
            schemaId = "prop",
            labelKey = "schema_prop",
            iconResId = R.drawable.ic_prop,
            assetPath = "catalog_templates/common/prop.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = ALL_PROJECT_TYPES
        ),
        CatalogSchemaDefinition(
            schemaId = "wardrobe",
            labelKey = "schema_wardrobe",
            iconResId = R.drawable.ic_wardrobe,
            assetPath = "catalog_templates/common/wardrobe.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = ALL_PROJECT_TYPES
        ),
        CatalogSchemaDefinition(
            schemaId = "vehicle",
            labelKey = "schema_vehicle",
            iconResId = R.drawable.ic_vehicle,
            assetPath = "catalog_templates/common/vehicle.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = ALL_PROJECT_TYPES
        ),
        CatalogSchemaDefinition(
            schemaId = "animal",
            labelKey = "schema_animal",
            iconResId = R.drawable.ic_animal,
            assetPath = "catalog_templates/common/animal.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = ALL_PROJECT_TYPES
        ),
        CatalogSchemaDefinition(
            schemaId = "organization",
            labelKey = "schema_organization",
            iconResId = R.drawable.ic_organization,
            assetPath = "catalog_templates/common/organization.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = ALL_PROJECT_TYPES
        ),
        CatalogSchemaDefinition(
            schemaId = "nationality",
            labelKey = "schema_nationality",
            iconResId = R.drawable.ic_catalog,
            assetPath = "catalog_templates/common/nationality.json",
            primaryFieldKey = "country_name",
            appliesToProjectTypeKeys = ALL_PROJECT_TYPES
        ),
        CatalogSchemaDefinition(
            schemaId = "disorder",
            labelKey = "schema_disorder",
            iconResId = R.drawable.ic_catalog,
            assetPath = "catalog_templates/common/disorders.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = ALL_PROJECT_TYPES
        ),
        CatalogSchemaDefinition(
            schemaId = "style_guide",
            labelKey = "schema_style_guide",
            iconResId = R.drawable.ic_style_guide,
            assetPath = "catalog_templates/common/style_guide.json",
            primaryFieldKey = "title",
            appliesToProjectTypeKeys = ALL_PROJECT_TYPES
        ),


        /* ─── TV SHOW + MOVIE ───────────────────── */
        CatalogSchemaDefinition(
            schemaId = "actor",
            labelKey = "schema_actor",
            iconResId = R.drawable.ic_actor,
            assetPath = "catalog_templates/tvshow_movie/actor.json",
            primaryFieldKey = "full_name",
            appliesToProjectTypeKeys = setOf("tvshow", "movie")
        ),
        CatalogSchemaDefinition(
            schemaId = "cast_member",
            labelKey = "schema_cast_member",
            iconResId = R.drawable.ic_cast,
            assetPath = "catalog_templates/tvshow_movie/cast_member.json",
            primaryFieldKey = "character_name",
            appliesToProjectTypeKeys = setOf("tvshow", "movie")
        ),
        CatalogSchemaDefinition(
            schemaId = "crew_member",
            labelKey = "schema_crew_member",
            iconResId = R.drawable.ic_crew,
            assetPath = "catalog_templates/tvshow_movie/crew_member.json",
            primaryFieldKey = "full_name",
            appliesToProjectTypeKeys = setOf("tvshow", "movie")
        ),
        CatalogSchemaDefinition(
            schemaId = "sound_fx",
            labelKey = "schema_sound_fx",
            iconResId = R.drawable.ic_soundfx,
            assetPath = "catalog_templates/tvshow_movie/sound_fx.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = setOf("tvshow", "movie")
        ),
        CatalogSchemaDefinition(
            schemaId = "visual_fx",
            labelKey = "schema_visual_fx",
            iconResId = R.drawable.ic_visualfx,
            assetPath = "catalog_templates/tvshow_movie/visual_fx.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = setOf("tvshow", "movie")
        ),
        CatalogSchemaDefinition(
            schemaId = "makeup_fx",
            labelKey = "schema_makeup_fx",
            iconResId = R.drawable.ic_makeupfx,
            assetPath = "catalog_templates/tvshow_movie/makeup_fx.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = setOf("tvshow", "movie")
        ),
        CatalogSchemaDefinition(
            schemaId = "hair_fx",
            labelKey = "schema_hair_fx",
            iconResId = R.drawable.ic_makeupfx,
            assetPath = "catalog_templates/tvshow_movie/hair_fx.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = setOf("tvshow", "movie")
        ),
        CatalogSchemaDefinition(
            schemaId = "set",
            labelKey = "schema_set",
            iconResId = R.drawable.ic_set,
            assetPath = "catalog_templates/tvshow_movie/set.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = setOf("tvshow", "movie")
        ),
        CatalogSchemaDefinition(
            schemaId = "stunt_fx",
            labelKey = "schema_stunt_fx",
            iconResId = R.drawable.ic_stunt,
            assetPath = "catalog_templates/tvshow_movie/stunt_fx.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = setOf("tvshow", "movie")
        ),

        /* ─── GAME ───────────────────── */
        CatalogSchemaDefinition(
            schemaId = "inventory_item",
            labelKey = "schema_inventory_item",
            iconResId = R.drawable.ic_inventory,
            assetPath = "catalog_templates/game/inventory_item.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = setOf("game")
        ),
        CatalogSchemaDefinition(
            schemaId = "skill",
            labelKey = "schema_skill",
            iconResId = R.drawable.ic_skill,
            assetPath = "catalog_templates/game/skill.json",
            primaryFieldKey = "name",
            appliesToProjectTypeKeys = setOf("game")
        ),

        /* ─── NOVEL ───────────────────── */


        /* ─── COMIC───────────────────── */
        CatalogSchemaDefinition(
            schemaId = "color_guide",
            labelKey = "schema_color_guide",
            iconResId = R.drawable.ic_color_guide,
            assetPath = "catalog_templates/comic/color_guide.json",
            primaryFieldKey = "title",
            appliesToProjectTypeKeys = setOf("comic")
        )
    )
}
