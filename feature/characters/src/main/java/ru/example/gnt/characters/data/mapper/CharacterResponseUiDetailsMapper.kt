package ru.example.gnt.characters.data.mapper

import ru.example.gnt.characters.presentation.detials.CharacterDetailsModel
import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.data.remote.model.CharactersResponseModel
import javax.inject.Inject

class CharacterResponseUiDetailsMapper @Inject constructor() :
    BaseMapper<CharactersResponseModel.Result, CharacterDetailsModel> {
    override fun mapTo(model: CharactersResponseModel.Result): CharacterDetailsModel = with(model) {
        CharacterDetailsModel(
            created = created,
            image = image,
            id = id,
            name = name,
            species = species,
            type = type,
            status = CharacterStatusEnum.find(status ?: CharacterStatusEnum.UNKNOWN.value),
            gender = CharacterGenderEnum.find(gender ?: CharacterGenderEnum.UNKNOWN.value),
            location = null,
            origin = null,
        )
    }

    override fun mapFrom(model: CharacterDetailsModel): CharactersResponseModel.Result =
        with(model) {
            CharactersResponseModel.Result(
                created = created,
                image = image,
                id = id,
                name = name,
                species = species,
                type = type,
                status = model.status?.value,
                gender = model.gender?.value,
            )
        }
}
