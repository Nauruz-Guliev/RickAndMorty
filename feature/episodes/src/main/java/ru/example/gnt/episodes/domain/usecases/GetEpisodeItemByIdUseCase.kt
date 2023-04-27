package ru.example.gnt.episodes.domain.usecases

import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.repository.EpisodesRepository
import javax.inject.Inject

internal class GetEpisodeItemByIdUseCase @Inject constructor(
    private val repository: EpisodesRepository
) {
    suspend operator fun invoke(id: Int): Result<EpisodeDetailsItem> {
        return repository.getEpisodeListItemById(id)
    }
}
