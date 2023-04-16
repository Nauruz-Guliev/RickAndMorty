package ru.example.gnt.characters.presentation.characters

import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.model.filter.CharacterFilterModel

data class CharactersFilterModel(
    var status: CharacterStatusEnum? = null,
    var gender: CharacterGenderEnum? = null,
    var species: String? = null,
    var name: String? = null,
    var type: String? = null,
)
