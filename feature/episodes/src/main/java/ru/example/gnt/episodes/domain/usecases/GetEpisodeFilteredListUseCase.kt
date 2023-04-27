package ru.example.gnt.episodes.domain.usecases

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.episodes.domain.model.EpisodeFilterModel
import ru.example.gnt.episodes.domain.repository.EpisodesRepository
import javax.inject.Inject

internal class GetEpisodeFilteredListUseCase @Inject constructor(
    private val repository: EpisodesRepository
) {
    suspend operator fun invoke(filterModel: EpisodeFilterModel): Flow<PagingData<EpisodeListItem>> {
        return repository.getEpisodesFilteredList(filterModel)
    }
}
