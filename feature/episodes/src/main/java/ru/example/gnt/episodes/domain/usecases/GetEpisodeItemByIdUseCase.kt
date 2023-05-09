package ru.example.gnt.episodes.domain.usecases

import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.repository.EpisodeDetailsRepository
import javax.inject.Inject

class GetEpisodeItemByIdUseCase @Inject constructor(
    private val repository: EpisodeDetailsRepository
) {
    suspend operator fun invoke(id: Int): Result<EpisodeDetailsItem> {
        return repository.getEpisodeDetailsItemById(id)
    }
}
