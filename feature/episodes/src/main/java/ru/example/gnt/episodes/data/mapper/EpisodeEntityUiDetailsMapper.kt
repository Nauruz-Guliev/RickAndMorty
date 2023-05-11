package ru.example.gnt.episodes.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import javax.inject.Inject

class EpisodeEntityUiDetailsMapper
@Inject constructor() : BaseMapper<EpisodeEntity, EpisodeDetailsItem> {
    override fun mapTo(model: EpisodeEntity): EpisodeDetailsItem =
        with(model) {
            return@with EpisodeDetailsItem(
                id = id,
                name = name,
                airDate = airDate,
                episode = episode,
                url = url,
                created = created,
            )
        }

    override fun mapFrom(model: EpisodeDetailsItem): EpisodeEntity =
        with(model) {
            return@with EpisodeEntity(
                id = id,
                name = name,
                airDate = airDate,
                episode = episode,
                url = url,
                created = created,
                characters = model.characters?.map { it.id.toString() } ?: listOf()
            )
        }
}
