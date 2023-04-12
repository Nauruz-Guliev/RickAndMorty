package ru.example.gnt.characters.presentation.characters

import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum

data class CharactersFilterModel(
    var status: CharacterStatusEnum? = null,
    var gender: CharacterGenderEnum? = null
)
