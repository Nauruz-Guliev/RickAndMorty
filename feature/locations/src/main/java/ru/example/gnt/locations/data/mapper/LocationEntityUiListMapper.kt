package ru.example.gnt.locations.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.model.locations.LocationListItem
import ru.example.gnt.data.local.entity.LocationEntity
import javax.inject.Inject

class LocationEntityUiListMapper
@Inject constructor(

) : BaseMapper<LocationEntity, LocationListItem> {
    override fun mapTo(model: LocationEntity): LocationListItem = with(model) {
        LocationListItem(
            id = id,
            name = name,
            type = type,
            dimension = dimension
        )
    }

    override fun mapFrom(model: LocationListItem): LocationEntity =
        with(model) {
            LocationEntity(
                id = id,
                name = name,
                type = type,
                dimension = dimension,
                created = null,
                residents = null,
                url = null
            )
        }

}
