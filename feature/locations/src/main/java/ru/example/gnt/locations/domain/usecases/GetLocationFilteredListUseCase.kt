package ru.example.gnt.locations.domain.usecases

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.locations.LocationListItem
import ru.example.gnt.locations.domain.repository.LocationListRepository
import ru.example.gnt.locations.presentation.list.LocationListFilterModel
import javax.inject.Inject

class GetLocationFilteredListUseCase @Inject constructor(
    private val repository: LocationListRepository
) {
    operator fun invoke(filterModel: LocationListFilterModel): Flow<PagingData<LocationListItem>> {
        return repository.getLocationFilteredList(filterModel)
    }
}
