package ru.example.gnt.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.model.locations.LocationListItem
import ru.example.gnt.data.remote.model.LocationsResponseModel
import javax.inject.Inject

class LocationResponseUiListItemMapper @Inject constructor() :
    BaseMapper<LocationsResponseModel.Result, LocationListItem> {
    override fun mapTo(model: LocationsResponseModel.Result): LocationListItem = with(model) {
        LocationListItem(
            id = id,
            name = name,
            type = type,
            dimension = dimension
        )
    }

    override fun mapFrom(model: LocationListItem): LocationsResponseModel.Result = with(model) {
        LocationsResponseModel.Result(
            id = id,
            name = name,
            type = type,
            dimension = dimension
        )
    }
}
