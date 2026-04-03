package com.meadow.feature.project.api

import androidx.compose.ui.res.stringResource
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


@Singleton
class SeriesSelectorImpl @Inject constructor(
    private val repo: SeriesRepositoryContract
) : SeriesSelector {

    override fun observeAvailableSeries(
        projectId: String?
    ): Flow<List<SeriesSelectorItem>> =
        repo.observeAllSeries()
            .map { seriesList ->
                seriesList
                    .filter { series ->
                        projectId == null || projectId in series.projectIds }
                    .map { series ->
                        SeriesSelectorItem(
                            id = series.id,
                            title = series.title.takeIf { it.isNotBlank() },
                            projectId = projectId
                        )
                    }
            }
}
