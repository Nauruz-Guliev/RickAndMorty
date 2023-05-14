package ru.example.gnt.episodes.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.di.qualifiers.BaseUrl
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import javax.inject.Inject

class EpisodeResponseUiDetailsMapper @Inject constructor(
    private val urlIdExtractor: UrlIdExtractor,
    @BaseUrl private val baseUrl: String
) :
    BaseMapper<EpisodesResponseModel.Result, EpisodeDetailsItem> {

    override fun mapTo(model: EpisodesResponseModel.Result): EpisodeDetailsItem =
        with(model) {
            return EpisodeDetailsItem(
                id = id,
                name = name,
                airDate = airDate,
                episode = episode,
                url = url,
                created = created
            )
        }


    override fun mapFrom(model: EpisodeDetailsItem): EpisodesResponseModel.Result =
        with(model) {
            return EpisodesResponseModel.Result(
                id = id,
                name = name,
                airDate = airDate,
                episode = episode,
                url = url,
                created = created,
                characters = characters?.map { baseUrl + "character/" + it.id.toString()}
            )
        }
}
