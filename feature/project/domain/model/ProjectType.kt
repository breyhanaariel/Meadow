package com.meadow.feature.project.domain.model

import androidx.annotation.StringRes
import com.meadow.feature.project.R as R

enum class ProjectType(
    val key: String,
    @StringRes val labelRes: Int
) {
    COMIC("comic", R.string.project_type_comic),
    NOVEL("novel", R.string.project_type_novel),
    TV_SHOW("tv_show", R.string.project_type_tv_show),
    MOVIE("movie", R.string.project_type_movie),
    GAME("game", R.string.project_type_game),
    UNKNOWN("unknown", R.string.project_type_unknown);

    companion object {
        fun fromKey(key: String?): ProjectType =
            entries.firstOrNull { it.key == key } ?: UNKNOWN
    }
}

