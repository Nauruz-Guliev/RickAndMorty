package ru.example.gnt.locations.data.mapper

import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.remote.model.LocationsResponseModel
import ru.example.gnt.locations.presentation.details.LocationDetailsModel
import javax.inject.Inject

class LocationsResponseUiDetailsMapper @Inject constructor(
    private val urlIdExtractor: UrlIdExtractor
) :
    BaseMapper<LocationsResponseModel.Result, LocationDetailsModel> {

    override fun mapTo(model: LocationsResponseModel.Result): LocationDetailsModel = with(model) {
        LocationDetailsModel(
            id = id,
            name = name,
            dimension = dimension,
            url = url,
            created = created,
            type = type,
            residents = residents?.map(urlIdExtractor::extract)
        )
    }

    override fun mapFrom(model: LocationDetailsModel): LocationsResponseModel.Result = with(model) {
        LocationsResponseModel.Result(
            id = id,
            name = name,
            dimension = dimension,
            url = url,
            created = created,
            type = type,
            residents = residents?.map {
                "https://rickandmortyapi.com/api/location/$it"
            }
        )
    }

}
