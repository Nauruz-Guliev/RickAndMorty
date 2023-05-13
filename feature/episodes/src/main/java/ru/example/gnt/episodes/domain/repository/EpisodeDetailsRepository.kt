package ru.example.gnt.episodes.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem

interface EpisodeDetailsRepository {
    suspend fun getEpisodeDetailsItemById(id: Int) : Flow<Result<EpisodeDetailsItem>>
}
