package com.meadow.core.ui.locale

import com.meadow.core.ui.R

object LanguageFlagResolver {
    fun flagFor(language: AppLanguage): Int = when (language) {

        /* ─── Tier 1: Massive Global Markets ───────────────────── */
        AppLanguage.ENGLISH -> R.drawable.ic_flag_en
        AppLanguage.CHINESE_SIMPLIFIED -> R.drawable.ic_flag_zhrcn
        AppLanguage.SPANISH -> R.drawable.ic_flag_es
        AppLanguage.HINDI -> R.drawable.ic_flag_hi
        AppLanguage.PORTUGUESE_BRAZIL -> R.drawable.ic_flag_pt_br

        /* ─── Tier 2: High Revenue / Strong Mobile Markets ───────────────────── */
        AppLanguage.RUSSIAN -> R.drawable.ic_flag_ru
        AppLanguage.JAPANESE -> R.drawable.ic_flag_ja
        AppLanguage.GERMAN -> R.drawable.ic_flag_de
        AppLanguage.FRENCH -> R.drawable.ic_flag_fr

        /* ─── Tier 3: Fast-Growing Android Regions ───────────────────── */
        AppLanguage.INDONESIAN -> R.drawable.ic_flag_id
        AppLanguage.VIETNAMESE -> R.drawable.ic_flag_vi
        AppLanguage.TURKISH -> R.drawable.ic_flag_tr
        AppLanguage.KOREAN -> R.drawable.ic_flag_ko

        /* ─── Tier 4: EU Expansion Markets ───────────────────── */
        AppLanguage.ITALIAN -> R.drawable.ic_flag_it
        AppLanguage.POLISH -> R.drawable.ic_flag_pl
        AppLanguage.UKRAINIAN -> R.drawable.ic_flag_uk
        AppLanguage.THAI -> R.drawable.ic_flag_th

        /* ─── Tier 5: Smaller Stable Markets ───────────────────── */
        AppLanguage.DUTCH -> R.drawable.ic_flag_nl
        AppLanguage.ROMANIAN -> R.drawable.ic_flag_ro
        AppLanguage.SWEDISH -> R.drawable.ic_flag_sv
        AppLanguage.CZECH -> R.drawable.ic_flag_cs
    }
}
