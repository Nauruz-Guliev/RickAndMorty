package ru.example.gnt.locations.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.data.local.entity.LocationEntity
import ru.example.gnt.data.mapper.LocationEntityResponseMapper
import ru.example.gnt.data.remote.model.LocationsResponseModel
import javax.inject.Inject

class LocationResponseInfoEntityMapper @Inject constructor(
    private val mapper: LocationEntityResponseMapper
) : BaseMapper<LocationsResponseModel, List<LocationEntity>> {
    override fun mapTo(model: LocationsResponseModel): List<LocationEntity> =
        model.results.map { mapper.mapTo(it!!) }

    override fun mapFrom(model: List<LocationEntity>): LocationsResponseModel =
        LocationsResponseModel(
            results = model.map(mapper::mapFrom)
        )
}
