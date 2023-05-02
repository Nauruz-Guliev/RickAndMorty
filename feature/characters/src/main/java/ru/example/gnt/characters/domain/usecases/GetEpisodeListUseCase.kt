package ru.example.gnt.characters.domain.usecases

import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.common.model.episodes.EpisodeListItem
import javax.inject.Inject

internal class GetEpisodeListUseCase @Inject constructor(
    private val repository: CharactersRepository
) {
    suspend operator fun invoke(ids: List<String>): List<EpisodeListItem> =
        repository.getEpisodeList(ids)
}
