package ru.example.gnt.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.di.qualifiers.BaseUrl
import ru.example.gnt.data.local.entity.LocationEntity
import ru.example.gnt.data.remote.model.LocationsResponseModel
import javax.inject.Inject

class LocationEntityResponseMapper @Inject constructor(
    private val urlIdExtractor: UrlIdExtractor,
    @BaseUrl private val baseUrl: String
) :
    BaseMapper<LocationsResponseModel.Result, LocationEntity> {

    override fun mapTo(model: LocationsResponseModel.Result): LocationEntity = LocationEntity(
        id = model.id,
        created = model.created,
        dimension = model.dimension,
        name = model.name,
        url = model.url,
        type = model.type,
        residents = model.residents?.map(urlIdExtractor::extract)
    )

    override fun mapFrom(model: LocationEntity): LocationsResponseModel.Result =
        LocationsResponseModel.Result(
            id = model.id,
            created = model.created,
            name = model.name,
            type = model.type,
            url = model.url,
            dimension = model.dimension,
            residents = model.residents?.map {
                "${baseUrl}character/$it"
            }
        )
}
