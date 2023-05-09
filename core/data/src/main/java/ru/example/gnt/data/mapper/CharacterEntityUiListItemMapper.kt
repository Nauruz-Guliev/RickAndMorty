package ru.example.gnt.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.data.local.entity.CharacterEntity
import javax.inject.Inject

class CharacterEntityUiListItemMapper @Inject constructor() :

    BaseMapper<CharacterEntity, CharacterListItem> {
    override fun mapTo(model: CharacterEntity): CharacterListItem = with(model) {
        CharacterListItem(
            id = id,
            name = name,
            species = species,
            gender = CharacterGenderEnum.find(gender ?: "unknown"),
            status = CharacterStatusEnum.find(status ?: "unknown"),
            image = image
        )
    }

    override fun mapFrom(model: CharacterListItem): CharacterEntity = with(model) {
        CharacterEntity(
            id = id,
            created = null,
            gender = gender?.value ?: "unknown",
            image = image,
            name = name,
            species = species,
            status = status?.value ?: "unknown",
            type = null,
            episode = null,
            locationId = null,
            originId = null
        )
    }
}
