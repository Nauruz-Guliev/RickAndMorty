package ru.example.gnt.characters.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.enums.CharacterGenderEnum
import ru.example.gnt.common.enums.CharacterStatusEnum
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.local.entity.CharacterEntity
import javax.inject.Inject

class CharacterUiEntityMapper @Inject constructor(
    private val urlIdExtractor: UrlIdExtractor
) : BaseMapper<CharacterEntity, ru.example.gnt.characters.presentation.list.model.CharactersUiModel.Single> {
    override fun mapTo(model: CharacterEntity): ru.example.gnt.characters.presentation.list.model.CharactersUiModel.Single {
        return ru.example.gnt.characters.presentation.list.model.CharactersUiModel.Single(
            created = model.created,
            id = model.id,
            image = model.image,
            name = model.name,
            species = model.species,
            type = model.type,
            url = model.url,
            status = CharacterStatusEnum.find(model.status),
            gender = CharacterGenderEnum.find(model.gender),
            episode = null
        )
    }

    override fun mapFrom(model: ru.example.gnt.characters.presentation.list.model.CharactersUiModel.Single): CharacterEntity {
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
            episode = model.episode?.map(
                urlIdExtractor::extract
            ) ?: listOf()
        )
    }

}
