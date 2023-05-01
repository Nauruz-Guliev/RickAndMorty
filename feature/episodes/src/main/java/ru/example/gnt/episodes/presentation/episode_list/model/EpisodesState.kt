package ru.example.gnt.episodes.presentation.episode_list.model

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.episodes.domain.model.EpisodeFilterModel

data class EpisodesState(
    val filter: EpisodeFilterModel = EpisodeFilterModel(),
    val search: String? = null,
    val episodesFlow: Flow<PagingData<EpisodeListItem>>? = null,
)
