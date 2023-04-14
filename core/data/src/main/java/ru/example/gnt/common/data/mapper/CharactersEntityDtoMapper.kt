package ru.example.gnt.common.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.data.local.entity.CharacterEntity
import ru.example.gnt.common.data.remote.model.Characters

//todo add other fields
object CharactersEntityDtoMapper : BaseMapper<Characters.Result, CharacterEntity> {
    override fun mapTo(model: Characters.Result): CharacterEntity =
        CharacterEntity(
            id = model.id,
            created = model.created,
            gender = model.gender ?: "unknown",
            image = model.image,
            name = model.name,
            species =  model.species,
            status = model.status ?: "unknown",
            type = model.type,
            url = model.url
        )

    override fun mapFrom(model: CharacterEntity): Characters.Result =
        Characters.Result(
            id = model.id,
            created = model.created,
            gender = model.gender,
            image = model.image,
            name = model.name,
            species =  model.species,
            status = model.status,
            type = model.type,
            url = model.url
        )

}
