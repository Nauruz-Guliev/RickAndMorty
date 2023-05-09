package ru.example.gnt.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.data.remote.model.CharactersResponseModel
import javax.inject.Inject

class CharacterResponseUiListItemMapper @Inject constructor() :
    BaseMapper<CharactersResponseModel.Result, CharacterListItem> {

    override fun mapTo(model: CharactersResponseModel.Result): CharacterListItem = with(model) {
        CharacterListItem(
            id = id,
            name = name,
            species = species,
            gender = CharacterGenderEnum.find(gender ?: "unknown"),
            status = CharacterStatusEnum.find(status ?: "unknown"),
            image = image
        )
    }

    override fun mapFrom(model: CharacterListItem): CharactersResponseModel.Result = with(model) {
        CharactersResponseModel.Result(
            id = id,
            name = name,
            species = species,
            gender = gender?.name,
            status = status?.value,
            image = image
        )
    }
}
