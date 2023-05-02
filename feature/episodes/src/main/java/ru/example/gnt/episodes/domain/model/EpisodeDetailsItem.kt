package ru.example.gnt.episodes.domain.model

import ru.example.gnt.common.base.BaseClass
import ru.example.gnt.common.model.characters.CharacterListItem

data class EpisodeDetailsItem(
    override val id: Int,
    override val name: String,
    val airDate: String,
    val episode: String,
    val characters: List<CharacterListItem>? = null,
    val url: String?,
    val created: String?,
) : BaseClass(id, name)
