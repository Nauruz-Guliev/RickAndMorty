package ru.example.gnt.locations.domain.repository

import io.reactivex.rxjava3.core.Single
import ru.example.gnt.locations.presentation.details.LocationDetailsModel

interface LocationDetailsRepository {
    fun getLocationDetailsItemById(id: Int): Single<LocationDetailsModel>
}
