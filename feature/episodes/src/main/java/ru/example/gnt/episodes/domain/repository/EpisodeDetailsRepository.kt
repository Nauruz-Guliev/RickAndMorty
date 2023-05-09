package ru.example.gnt.episodes.domain.repository

import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem

interface EpisodeDetailsRepository {
    suspend fun getEpisodeDetailsItemById(id: Int) : Result<EpisodeDetailsItem>
}
