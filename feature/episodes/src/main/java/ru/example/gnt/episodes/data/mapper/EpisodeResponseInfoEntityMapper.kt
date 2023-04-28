package ru.example.gnt.episodes.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import javax.inject.Inject

class EpisodeResponseInfoEntityMapper @Inject constructor(
    private val mapper: EpisodeEntityResponseMapper
) : BaseMapper<EpisodesResponseModel, List<EpisodeEntity>> {
    override fun mapTo(model: EpisodesResponseModel): List<EpisodeEntity> =
        model.results.map(mapper::mapTo)

    override fun mapFrom(model: List<EpisodeEntity>): EpisodesResponseModel =
        EpisodesResponseModel(results = model.map(mapper::mapFrom))

}
