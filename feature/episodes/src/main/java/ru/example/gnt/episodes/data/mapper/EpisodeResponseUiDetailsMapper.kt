package ru.example.gnt.episodes.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import javax.inject.Inject

class EpisodeResponseUiDetailsMapper @Inject constructor(

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
                characters = listOf()
            )
        }
}
