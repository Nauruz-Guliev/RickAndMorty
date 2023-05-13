package ru.example.gnt.locations.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.locations.LocationListItem
import ru.example.gnt.locations.presentation.list.LocationListFilterModel

interface LocationListRepository {
    fun getLocationFilteredList(filter: LocationListFilterModel): Flow<PagingData<LocationListItem>>
}
