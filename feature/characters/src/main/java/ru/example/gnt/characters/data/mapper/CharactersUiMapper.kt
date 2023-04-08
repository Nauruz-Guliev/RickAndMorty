package ru.example.gnt.characters.data.mapper

import ru.example.gnt.characters.domain.model.CharactersUiModel
import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.local.models.Characters

internal object CharactersUiMapper : BaseMapper<Characters, CharactersUiModel> {

    override fun mapTo(model: Characters): CharactersUiModel {
        return CharactersUiModel(
            info = CharactersUiModel.Info(
                count = model.info.count,
                next = model.info.next,
                pages = model.info.pages,
                prev = model.info.prev
            ),
            singles = model.singles.map { character ->
                CharactersUiModel.Single(
                    created = character.created,
                    episode = character.episode,
                    gender = CharacterGenderEnum.find(character.gender)
                        ?: CharacterGenderEnum.UNKNOWN,
                    id = character.id,
                    image = character.image,
                    location = CharactersUiModel.Single.Location(
                        name = character.location.name,
                        url = character.location.url
                    ),
                    name = character.name,
                    origin = CharactersUiModel.Single.Origin(
                        name = character.origin.name,
                        url = character.origin.url
                    ),
                    status = CharacterStatusEnum.find(character.status)
                        ?: CharacterStatusEnum.UNKNOWN,
                    type = character.type,
                    url = character.url,
                    species = character.species
                )
            }
        )
    }

    override fun mapFrom(model: CharactersUiModel): Characters {
        return Characters(
            info = Characters.Info(
                count = model.info.count,
                next = model.info.next,
                pages = model.info.pages,
                prev = model.info.prev
            ),
            singles = model.singles.map { character ->
                Characters.Single(
                    created = character.created,
                    episode = character.episode,
                    gender = character.gender.n,
                    id = character.id,
                    image = character.image,
                    location = Characters.Single.Location(
                        name = character.location.name,
                        url = character.location.url
                    ),
                    name = character.name,
                    origin = Characters.Single.Origin(
                        name = character.origin.name,
                        url = character.origin.url
                    ),
                    status = character.status.n,
                    type = character.type,
                    url = character.url,
                    species = character.species
                )
            }
        )
    }
}
