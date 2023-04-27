package ru.example.gnt.common.model.characters

import ru.example.gnt.common.base.BaseClass
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum

data class CharacterListItem(
    override val id: Int,
    override val name: String,
    val species: String,
    val gender: CharacterGenderEnum,
    val status: CharacterStatusEnum,
    val image: String
) : BaseClass(id, name)
