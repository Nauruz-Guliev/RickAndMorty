package ru.example.gnt.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.di.qualifiers.BaseUrl
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import javax.inject.Inject

class EpisodeEntityResponseMapper @Inject constructor(
    private val urlIdExtractor: UrlIdExtractor,
    @BaseUrl private val baseUrl: String
) : BaseMapper<EpisodesResponseModel.Result, EpisodeEntity> {

    override fun mapTo(model: EpisodesResponseModel.Result): EpisodeEntity = EpisodeEntity(
        id = model.id,
        airDate = model.airDate,
        created = model.created,
        episode = model.episode,
        name = model.name,
        url = model.url,
        characters = model.characters.map(urlIdExtractor::extract)
    )

    override fun mapFrom(model: EpisodeEntity): EpisodesResponseModel.Result =
        EpisodesResponseModel.Result(
            id = model.id,
            airDate = model.airDate,
            created = model.created,
            episode = model.episode,
            name = model.name,
            url = model.url,
            characters = model.characters.map {
                "${baseUrl}character/$it"
            }
        )
}
