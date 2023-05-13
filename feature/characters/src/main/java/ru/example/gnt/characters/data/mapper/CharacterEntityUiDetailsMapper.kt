package ru.example.gnt.characters.data.mapper

import ru.example.gnt.characters.presentation.detials.CharacterDetailsModel
import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.data.local.entity.CharacterEntity
import javax.inject.Inject


class CharacterEntityUiDetailsMapper @Inject constructor(): BaseMapper<CharacterEntity, CharacterDetailsModel> {

    override fun mapTo(model: CharacterEntity): CharacterDetailsModel = with(model) {
        CharacterDetailsModel(
            image = image,
            created = created,
            species = species,
            status = CharacterStatusEnum.find(status ?: "unknown"),
            gender = CharacterGenderEnum.find(gender ?: "unknown"),
            location = null,
            type = type,
            origin = null,
            episode = null,
            name = name,
            id = id
        )
    }

    override fun mapFrom(model: CharacterDetailsModel): CharacterEntity = with(model) {
        CharacterEntity(
            image = image,
            created = created,
            species = species,
            status = status?.value,
            gender = gender?.value,
            locationId = null,
            type = type,
            originId = null,
            episode = null,
            name = name,
            id = id,
        )
    }
}
