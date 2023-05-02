package ru.example.gnt.characters.presentation.list.model


import ru.example.gnt.common.base.BaseClass
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.model.episodes.EpisodeListItem


data class CharactersUiModel(
    val singles: List<Single>? = null
) {

    data class Single(
        val created: String,
        val episode: List<EpisodeListItem>? = null,
        val gender: CharacterGenderEnum? = null,
        override val id: Int,
        val image: String,
        val location: Location? = null,
        override val name: String,
        val origin: Origin? = null,
        val species: String,
        val status: CharacterStatusEnum? = null,
        val type: String,
        val url: String,
    ) : BaseClass(id, name) {
        data class Location(
            val name: String?,
            val url: String?
        )

        data class Origin(
            val name: String?,
            val url: String?
        )
    }
}
