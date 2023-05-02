package ru.example.gnt.locations.domain.repository

import ru.example.gnt.locations.presentation.details.LocationDetailsModel

interface LocationDetailsRepository {
    fun getLocationDetailsItemById(id: Int): Result<LocationDetailsModel>
}
