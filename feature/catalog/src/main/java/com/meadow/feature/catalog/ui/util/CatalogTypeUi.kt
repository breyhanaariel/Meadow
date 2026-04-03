package com.meadow.feature.catalog.ui.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.catalog.R as R
import com.meadow.feature.catalog.domain.model.CatalogType

@StringRes
fun CatalogType.labelRes(): Int =
    when (this) {
        CatalogType.ANIMAL -> R.string.catalog_type_animal
        CatalogType.CHARACTER -> R.string.catalog_type_character
        CatalogType.LOCATION -> R.string.catalog_type_location
        CatalogType.ORGANIZATION -> R.string.catalog_type_organization
        CatalogType.PROP -> R.string.catalog_type_prop
        CatalogType.VEHICLE -> R.string.catalog_type_vehicle
        CatalogType.WARDROBE -> R.string.catalog_type_wardrobe

        CatalogType.COLOR_GUIDE -> R.string.catalog_type_color_guide
        CatalogType.INVENTORY_ITEM -> R.string.catalog_type_inventory
        CatalogType.SKILL -> R.string.catalog_type_skill
        CatalogType.STYLE_GUIDE -> R.string.catalog_type_style_guide

        CatalogType.ACTOR  -> R.string.catalog_type_actor
        CatalogType.CAST_MEMBER  -> R.string.catalog_type_cast_member
        CatalogType.HAIR_FX  -> R.string.catalog_type_hair_fx
        CatalogType.MAKEUP_FX -> R.string.catalog_type_makeup_fx
        CatalogType.SET  -> R.string.catalog_type_set
        CatalogType.SOUND_FX  -> R.string.catalog_type_sound_fx
        CatalogType.STUNT_FX  -> R.string.catalog_type_stunt_fx
        CatalogType.VISUAL_FX -> R.string.catalog_type_cast

        CatalogType.UNKNOWN -> R.string.catalog_type_unknown
    }

@DrawableRes
fun CatalogType.iconRes(): Int =
    when (this) {
        CatalogType.ANIMAL -> R.string.catalog_type_animal
        CatalogType.CHARACTER -> R.string.catalog_type_character
        CatalogType.LOCATION -> R.string.catalog_type_location
        CatalogType.ORGANIZATION -> R.string.catalog_type_organization
        CatalogType.PROP -> R.string.catalog_type_prop
        CatalogType.VEHICLE -> R.string.catalog_type_vehicle
        CatalogType.WARDROBE -> R.string.catalog_type_wardrobe

        CatalogType.COLOR_GUIDE -> R.string.catalog_type_color_guide
        CatalogType.INVENTORY_ITEM -> R.string.catalog_type_inventory
        CatalogType.SKILL -> R.string.catalog_type_skill
        CatalogType.STYLE_GUIDE -> R.string.catalog_type_style_guide

        CatalogType.ACTOR  -> R.string.catalog_type_actor
        CatalogType.CAST_MEMBER  -> R.string.catalog_type_cast_member
        CatalogType.HAIR_FX  -> R.string.catalog_type_hair_fx
        CatalogType.MAKEUP_FX -> R.string.catalog_type_makeup_fx
        CatalogType.SET  -> R.string.catalog_type_set
        CatalogType.SOUND_FX  -> R.string.catalog_type_sound_fx
        CatalogType.STUNT_FX  -> R.string.catalog_type_stunt_fx
        CatalogType.VISUAL_FX -> R.string.catalog_type_cast

        CatalogType.UNKNOWN -> R.string.catalog_type_unknown

        CatalogType.ANIMAL -> R.drawable.ic_animal
        CatalogType.CHARACTER -> R.drawable.ic_character
        CatalogType.LOCATION -> R.drawable.ic_location
        CatalogType.ORGANIZATION -> R.drawable.ic_organization
        CatalogType.PROP -> R.drawable.ic_prop
        CatalogType.VEHICLE -> R.drawable.ic_vehicle
        CatalogType.WARDROBE -> R.drawable.ic_wardrobe
        CatalogType.COLOR_GUIDE -> R.drawable.ic_color_guide
        CatalogType.INVENTORY_ITEM -> R.drawable.ic_inventory
        CatalogType.SKILL -> R.drawable.ic_skill
        CatalogType.STYLE_GUIDE -> R.drawable.ic_style_guide
        CatalogType.ACTOR -> R.drawable.ic_actor
        CatalogType.CAST_MEMBER -> R.drawable.ic_cast
        CatalogType.HAIR_FX -> R.drawable.ic_hairfx
        CatalogType.MAKEUP_FX -> R.drawable.ic_makeupfx
        CatalogType.SET -> R.drawable.ic_set
        CatalogType.SOUND_FX -> R.drawable.ic_soundfx
        CatalogType.STUNT_FX -> R.drawable.ic_stunt
        CatalogType.VISUAL_FX -> R.drawable.ic_visualfx
        else -> R.drawable.ic_item
    }
