package ru.example.gnt.episodes.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.di.qualifiers.BaseUrl
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import javax.inject.Inject

class EpisodeEntityResponseMapper @Inject constructor(
    private val urlIdExtractor: UrlIdExtractor
) : BaseMapper<
        EpisodeEntity, EpisodesResponseModel.Result> {
    override fun mapTo(model: EpisodeEntity): EpisodesResponseModel.Result =
        with(model) {
            return@with EpisodesResponseModel.Result(
                id = id,
                name = name,
                airDate = airDate,
                created = created,
                episode = episode,
                url = url,
                characters = characters.map {
                    "https://rickandmortyapi.com/api/character/$it"
                }
            )
        }

    override fun mapFrom(model: EpisodesResponseModel.Result): EpisodeEntity =
        with(model) {
            return@with EpisodeEntity(
                id = id,
                name = name,
                airDate = airDate,
                created = created,
                episode = episode,
                url = url,
                characters = characters.map(urlIdExtractor::extract)
            )
        }
}