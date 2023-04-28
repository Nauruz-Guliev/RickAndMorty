package ru.example.gnt.episodes.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.utils.RetrofitResult
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.repository.EpisodesRepository
import javax.inject.Inject

internal class GetEpisodeItemByIdUseCase @Inject constructor(
    private val repository: EpisodesRepository
) {
    suspend operator fun invoke(id: Int): Flow<RetrofitResult<EpisodeDetailsItem>?> {
        return repository.getEpisodeListItemById(id)
    }
}
