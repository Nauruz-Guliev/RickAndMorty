package ru.example.gnt.common.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.data.local.entity.CharacterEntity
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.model.ui.characters.CharactersUiModel
import ru.example.gnt.common.utils.UrlIdExtractor

object CharacterUiEntityMapper : BaseMapper<CharacterEntity, CharactersUiModel.Single> {
    override fun mapTo(model: CharacterEntity): CharactersUiModel.Single {
        return CharactersUiModel.Single(
            created = model.created,
            id = model.id,
            image = model.image,
            name = model.name,
            species = model.species,
            type = model.type,
            url = model.url,
            status = CharacterStatusEnum.find(model.status),
            gender = CharacterGenderEnum.find(model.gender),
            episode = model.episode.map {
                "https://rickandmortyapi.com/api/location/$it"
            }
        )
    }

    override fun mapFrom(model: CharactersUiModel.Single): CharacterEntity {
        return CharacterEntity(
            created = model.created,
            id = model.id,
            image = model.image,
            name = model.name,
            species = model.species,
            type = model.type,
            url = model.url,
            gender = model.gender?.n ?: "unknown",
            status = model.status?.get ?: "unknown",
            episode = model.episode?.map {
                UrlIdExtractor.extract(it).toString()
            } ?: listOf()
        )
    }

}
