package ru.example.gnt.common.model.filter

import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum

data class CharacterFilterModel(
    var name: String? = null,
    var statusList: List<CharacterStatusEnum>? = null,
    var species: String? = null,
    var type: String? = null,
    var genderList: List<CharacterGenderEnum>? = null
)
