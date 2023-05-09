package ru.example.gnt.locations.domain.usecases

import io.reactivex.rxjava3.core.Single
import ru.example.gnt.locations.domain.repository.LocationDetailsRepository
import ru.example.gnt.locations.presentation.details.LocationDetailsModel
import javax.inject.Inject

class GetLocationDetailsUseCase @Inject constructor(
    private val repository: LocationDetailsRepository
) {
    operator fun invoke(id: Int): Single<LocationDetailsModel> =
        repository.getLocationDetailsItemById(id)
}
