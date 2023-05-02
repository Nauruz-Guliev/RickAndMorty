package ru.example.gnt.locations.presentation.list

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.model.locations.LocationListItem

data class LocationListState(
    val filter: LocationListFilterModel = LocationListFilterModel(),
    val search: String? = null,
    val locationsFlow: Flow<PagingData<LocationListItem>>? = null,
)
