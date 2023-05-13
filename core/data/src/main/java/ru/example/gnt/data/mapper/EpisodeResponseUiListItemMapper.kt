package ru.example.gnt.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import javax.inject.Inject

class EpisodeResponseUiListItemMapper
@Inject constructor() : BaseMapper<EpisodesResponseModel.Result, EpisodeListItem> {

    override fun mapTo(model: EpisodesResponseModel.Result): EpisodeListItem = with(model) {
        EpisodeListItem(
            id = id,
            name = name,
            episode = episode,
            airDate = airDate
        )
    }

    override fun mapFrom(model: EpisodeListItem): EpisodesResponseModel.Result = with(model) {
        EpisodesResponseModel.Result(
            id = id,
            name = name,
            episode = episode,
            airDate = airDate
        )
    }
}
