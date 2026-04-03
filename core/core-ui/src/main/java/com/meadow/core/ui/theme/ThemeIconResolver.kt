package com.meadow.core.ui.theme

import com.meadow.core.ui.R as R

object ThemeIconResolver {

    fun wordmark(theme: MeadowThemeVariant): Int = when (theme) {
        MeadowThemeVariant.Lavender -> R.drawable.ic_wordmark_lavender
        MeadowThemeVariant.Blush -> R.drawable.ic_wordmark_blush
        MeadowThemeVariant.Periwinkle -> R.drawable.ic_wordmark_periwinkle
        MeadowThemeVariant.Mint -> R.drawable.ic_wordmark_mint
        MeadowThemeVariant.Peach -> R.drawable.ic_wordmark_peach
        MeadowThemeVariant.Cream -> R.drawable.ic_wordmark_cream
    }

    fun planet(theme: MeadowThemeVariant): Int = when (theme) {
        MeadowThemeVariant.Lavender -> R.drawable.ic_topbar_planet_lavender
        MeadowThemeVariant.Blush -> R.drawable.ic_topbar_planet_blush
        MeadowThemeVariant.Periwinkle -> R.drawable.ic_topbar_planet_periwinkle
        MeadowThemeVariant.Mint -> R.drawable.ic_topbar_planet_mint
        MeadowThemeVariant.Peach -> R.drawable.ic_topbar_planet_peach
        MeadowThemeVariant.Cream -> R.drawable.ic_topbar_planet_cream
    }

    fun chat(theme: MeadowThemeVariant): Int = when (theme) {
        MeadowThemeVariant.Lavender -> R.drawable.ic_topbar_chat_lavender
        MeadowThemeVariant.Blush -> R.drawable.ic_topbar_chat_blush
        MeadowThemeVariant.Periwinkle -> R.drawable.ic_topbar_chat_periwinkle
        MeadowThemeVariant.Mint -> R.drawable.ic_topbar_chat_mint
        MeadowThemeVariant.Peach -> R.drawable.ic_topbar_chat_peach
        MeadowThemeVariant.Cream -> R.drawable.ic_topbar_chat_cream
    }

    fun crescent(theme: MeadowThemeVariant): Int = when (theme) {
        MeadowThemeVariant.Lavender -> R.drawable.ic_topbar_crescent_lavender
        MeadowThemeVariant.Blush -> R.drawable.ic_topbar_crescent_blush
        MeadowThemeVariant.Periwinkle -> R.drawable.ic_topbar_crescent_periwinkle
        MeadowThemeVariant.Mint -> R.drawable.ic_topbar_crescent_mint
        MeadowThemeVariant.Peach -> R.drawable.ic_topbar_crescent_peach
        MeadowThemeVariant.Cream -> R.drawable.ic_topbar_crescent_cream
    }

    fun kebabmenu(theme: MeadowThemeVariant): Int = when (theme) {
        MeadowThemeVariant.Lavender -> R.drawable.ic_kebab_menu_lavender
        MeadowThemeVariant.Blush -> R.drawable.ic_kebab_menu_blush
        MeadowThemeVariant.Periwinkle -> R.drawable.ic_kebab_menu_periwinkle
        MeadowThemeVariant.Mint -> R.drawable.ic_kebab_menu_mint
        MeadowThemeVariant.Peach -> R.drawable.ic_kebab_menu_peach
        MeadowThemeVariant.Cream -> R.drawable.ic_kebab_menu_cream
    }

    fun fabmenu(theme: MeadowThemeVariant): Int = when (theme) {
        MeadowThemeVariant.Lavender -> R.drawable.ic_fabmenu_lavender
        MeadowThemeVariant.Blush -> R.drawable.ic_fabmenu_blush
        MeadowThemeVariant.Periwinkle -> R.drawable.ic_fabmenu_periwinkle
        MeadowThemeVariant.Mint -> R.drawable.ic_fabmenu_mint
        MeadowThemeVariant.Peach -> R.drawable.ic_fabmenu_peach
        MeadowThemeVariant.Cream -> R.drawable.ic_fabmenu_cream
    }
}
