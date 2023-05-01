package ru.example.gnt.episodes.domain.usecases

import ru.example.gnt.common.utils.DataResource
import ru.example.gnt.episodes.data.EpisodeDetailsRepositoryImpl
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.repository.EpisodeDetailsRepository
import ru.example.gnt.episodes.domain.repository.EpisodeListRepository
import javax.inject.Inject

class GetEpisodeItemByIdUseCase @Inject constructor(
    private val repository: EpisodeDetailsRepository
) {
    suspend operator fun invoke(id: Int): DataResource<EpisodeDetailsItem> {
        return repository.getEpisodeListItemById(id)
    }
}
