package ru.example.gnt.characters.presentation.detials

import ru.example.gnt.common.base.BaseClass
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.common.model.locations.LocationListItem

data class CharacterDetailsModel(
    val created: String? = null,
    var episode: List<EpisodeListItem>? = null,
    val image: String,
    val species: String,
    val status: CharacterStatusEnum? = null,
    val gender: CharacterGenderEnum? = null,
    var location: LocationListItem? = null,
    val type: String? = null,
    var origin: LocationListItem? = null,
    override val name: String,
    override val id: Int,
) : BaseClass(id, name)
