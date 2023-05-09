package ru.example.gnt.locations.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.local.entity.LocationEntity
import ru.example.gnt.locations.presentation.details.LocationDetailsModel
import javax.inject.Inject

class LocationEntityUiDetailsMapper
@Inject constructor(
    private val urlIdExtractor: UrlIdExtractor
) : BaseMapper<LocationEntity, LocationDetailsModel> {
    override fun mapTo(model: LocationEntity): LocationDetailsModel = with(model) {
        LocationDetailsModel(
            id = id,
            name = name,
            type = type,
            dimension = dimension,
            url = url,
            created = created,
            residents = null
        )
    }

    override fun mapFrom(model: LocationDetailsModel): LocationEntity = with(model) {
        LocationEntity(
            id = id,
            name = name,
            type = type,
            dimension = dimension,
            url = url,
            created = created,
            residents = residents?.map {
                "https://rickandmortyapi.com/api/location/${it.id}"
            }
        )
    }
}
