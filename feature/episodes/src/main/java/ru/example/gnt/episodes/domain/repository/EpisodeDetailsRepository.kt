package ru.example.gnt.episodes.domain.repository

import ru.example.gnt.common.utils.DataResource
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem

interface EpisodeDetailsRepository {
    suspend fun getEpisodeListItemById(id: Int) : DataResource<EpisodeDetailsItem>
}
