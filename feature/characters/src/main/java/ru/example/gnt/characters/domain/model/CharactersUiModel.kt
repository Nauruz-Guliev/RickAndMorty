package ru.example.gnt.characters.domain.model


import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import java.util.*


internal data class CharactersUiModel(
    val info: Info,
    val singles: List<Single>
) {
    internal data class Info(
        val count: Int,
        val next: String,
        val pages: Int,
        val prev: Any?
    )

    internal data class Single(
        val created: String,
        val episode: List<String>,
        val gender: CharacterGenderEnum,
        val id: Int,
        val image: String,
        val location: Location,
        val name: String,
        val origin: Origin,
        val species: String,
        val status: CharacterStatusEnum,
        val type: String,
        val url: String,
    ) {
        internal data class Location(
            val name: String,
            val url: String
        )

        internal data class Origin(
            val name: String,
            val url: String
        )
    }
}
