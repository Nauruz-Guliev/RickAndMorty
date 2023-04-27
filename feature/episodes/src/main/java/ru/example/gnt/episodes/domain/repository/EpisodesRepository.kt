package ru.example.gnt.episodes.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.model.EpisodeFilterModel

internal interface EpisodesRepository {
    suspend fun getEpisodeListItemById(id: Int) : Result<EpisodeDetailsItem>
    fun getEpisodesFilteredList(filter: EpisodeFilterModel): Flow<PagingData<EpisodeListItem>>
}
