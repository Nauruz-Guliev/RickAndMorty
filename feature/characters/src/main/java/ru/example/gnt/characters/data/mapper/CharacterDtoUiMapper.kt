package ru.example.gnt.characters.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.data.remote.model.CharactersResponseModel
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum

object CharacterDtoUiMapper :
    BaseMapper<CharactersResponseModel.Result, ru.example.gnt.characters.presentation.list.model.CharactersUiModel.Single> {
    override fun mapTo(model: CharactersResponseModel.Result): ru.example.gnt.characters.presentation.list.model.CharactersUiModel.Single =
        ru.example.gnt.characters.presentation.list.model.CharactersUiModel.Single(
            created = model.created,
            episode = model.episode,
            gender = CharacterGenderEnum.find(model.gender ?: "unknown")
                ?: CharacterGenderEnum.UNKNOWN,
            id = model.id,
            image = model.image,
            location = ru.example.gnt.characters.presentation.list.model.CharactersUiModel.Single.Location(
                name = model.location?.name,
                url = model.location?.url
            ),
            name = model.name,
            origin = ru.example.gnt.characters.presentation.list.model.CharactersUiModel.Single.Origin(
                name = model.origin?.name,
                url = model.origin?.url
            ),
            status = CharacterStatusEnum.find(model.status?: "unknown")
                ?: CharacterStatusEnum.UNKNOWN,
            type = model.type,
            url = model.url,
            species = model.species
        )

    override fun mapFrom(model: ru.example.gnt.characters.presentation.list.model.CharactersUiModel.Single): CharactersResponseModel.Result =
        CharactersResponseModel.Result(
            created = model.created,
            episode = model.episode,
            gender = model.gender?.n,
            id = model.id,
            image = model.image,
            location = CharactersResponseModel.Result.Location(
                name = model.location?.name,
                url = model.location?.url
            ),
            name = model.name,
            origin = CharactersResponseModel.Result.Origin(
                name = model.origin?.name,
                url = model.origin?.url
            ),
            status = model.status?.get,
            type = model.type,
            url = model.url,
            species = model.species
        )

}
