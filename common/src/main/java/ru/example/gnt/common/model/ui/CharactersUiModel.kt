package ru.example.gnt.common.model.ui


import ru.example.gnt.common.base.BaseClass
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum


data class CharactersUiModel(
    val info: Info? = null,
    val singles: List<Single>? = null
) {
    data class Info(
        val count: Int?,
        val next: String?,
        val pages: Int?,
        val prev: Any?
    )

    data class Single(
        val created: String,
        val episode: List<String>? = null,
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
