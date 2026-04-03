package com.meadow.feature.project.ui.util

import androidx.annotation.DrawableRes
import com.meadow.feature.project.domain.model.ProjectType
import com.meadow.feature.project.R as ProjectR

@DrawableRes
fun fallbackProjectImage(type: ProjectType): Int =
    when (type) {
        ProjectType.COMIC -> ProjectR.drawable.ic_comic
        ProjectType.GAME -> ProjectR.drawable.ic_game
        ProjectType.MOVIE -> ProjectR.drawable.ic_movie
        ProjectType.TV_SHOW -> ProjectR.drawable.ic_tvshow
        ProjectType.NOVEL -> ProjectR.drawable.ic_novel
        ProjectType.UNKNOWN -> ProjectR.drawable.ic_project
    }
