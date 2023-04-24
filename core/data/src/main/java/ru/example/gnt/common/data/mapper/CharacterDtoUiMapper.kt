package ru.example.gnt.common.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.data.remote.model.Characters
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.model.ui.characters.CharactersUiModel

object CharacterDtoUiMapper : BaseMapper<Characters.Result, CharactersUiModel.Single> {
    override fun mapTo(model: Characters.Result): CharactersUiModel.Single =
        CharactersUiModel.Single(
            created = model.created,
            episode = model.episode,
            gender = CharacterGenderEnum.find(model.gender?: "unknown")
                ?: CharacterGenderEnum.UNKNOWN,
            id = model.id,
            image = model.image,
            location = CharactersUiModel.Single.Location(
                name = model.location?.name,
                url = model.location?.url
            ),
            name = model.name,
            origin = CharactersUiModel.Single.Origin(
                name = model.origin?.name,
                url = model.origin?.url
            ),
            status = CharacterStatusEnum.find(model.status?: "unknown")
                ?: CharacterStatusEnum.UNKNOWN,
            type = model.type,
            url = model.url,
            species = model.species
        )

    override fun mapFrom(model: CharactersUiModel.Single): Characters.Result =
        Characters.Result(
            created = model.created,
            episode = model.episode,
            gender = model.gender?.n,
            id = model.id,
            image = model.image,
            location = Characters.Result.Location(
                name = model.location?.name,
                url = model.location?.url
            ),
            name = model.name,
            origin = Characters.Result.Origin(
                name = model.origin?.name,
                url = model.origin?.url
            ),
            status = model.status?.get,
            type = model.type,
            url = model.url,
            species = model.species
        )

}
