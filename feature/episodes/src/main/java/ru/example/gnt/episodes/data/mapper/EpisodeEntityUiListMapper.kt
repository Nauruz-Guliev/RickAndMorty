package ru.example.gnt.episodes.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.data.local.entity.EpisodeEntity
import javax.inject.Inject

class EpisodeEntityUiListMapper
@Inject constructor(

) : BaseMapper<EpisodeEntity, EpisodeListItem> {
    override fun mapTo(model: EpisodeEntity): EpisodeListItem = with(model) {
        return@with EpisodeListItem(
            id = id,
            name = name,
            episode = episode,
            airDate = airDate
        )
    }

    override fun mapFrom(model: EpisodeListItem): EpisodeEntity = with(model) {
        return@with EpisodeEntity(
            id = id,
            airDate = airDate,
            created = "",
            characters = listOf(),
            episode = episode,
            name = name,
            url = ""
        )
    }
}