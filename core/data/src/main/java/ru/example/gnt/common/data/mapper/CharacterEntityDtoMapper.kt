package ru.example.gnt.common.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.data.local.entity.CharacterEntity
import ru.example.gnt.common.data.remote.model.Characters
import ru.example.gnt.common.utils.UrlIdExtractor

//todo add other fields
object CharacterEntityDtoMapper : BaseMapper<Characters.Result, CharacterEntity> {
    override fun mapTo(model: Characters.Result): CharacterEntity =
        CharacterEntity(
            id = model.id,
            created = model.created,
            gender = model.gender ?: "unknown",
            image = model.image,
            name = model.name,
            species = model.species,
            status = model.status ?: "unknown",
            type = model.type,
            url = model.url,
            episode = model.episode?.map {
                UrlIdExtractor.extract(it).toString()
            } ?: listOf()
        )

    override fun mapFrom(model: CharacterEntity): Characters.Result =
        Characters.Result(
            id = model.id,
            created = model.created,
            gender = model.gender,
            image = model.image,
            name = model.name,
            species = model.species,
            status = model.status,
            type = model.type,
            url = model.url,
            episode = model.episode.map {
                "https://rickandmortyapi.com/api/location/$it"
            }
        )

}
