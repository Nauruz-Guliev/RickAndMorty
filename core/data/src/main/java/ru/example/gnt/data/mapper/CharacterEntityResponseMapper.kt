package ru.example.gnt.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.di.qualifiers.BaseUrl
import ru.example.gnt.data.local.entity.CharacterEntity
import ru.example.gnt.data.remote.model.CharactersResponseModel
import javax.inject.Inject

class CharacterEntityResponseMapper
@Inject constructor(
    private val urlIdExtractor: UrlIdExtractor,
    @BaseUrl private val baseUrl: String,
) : BaseMapper<CharactersResponseModel.Result, CharacterEntity> {
    override fun mapTo(model: CharactersResponseModel.Result): CharacterEntity =
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
            episode = model.episode?.map(urlIdExtractor::extract) ?: listOf()
        )

    override fun mapFrom(model: CharacterEntity): CharactersResponseModel.Result =
        CharactersResponseModel.Result(
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
                "${baseUrl}location/$it"
            }
        )

}
