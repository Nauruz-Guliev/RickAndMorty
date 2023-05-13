package ru.example.gnt.episodes.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.repository.EpisodeDetailsRepository
import javax.inject.Inject

class GetEpisodeItemByIdUseCase @Inject constructor(
    private val repository: EpisodeDetailsRepository
) {
    suspend operator fun invoke(id: Int): Flow<Result<EpisodeDetailsItem>> {
        return repository.getEpisodeDetailsItemById(id)
    }
}
