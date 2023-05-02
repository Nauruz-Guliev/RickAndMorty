package ru.example.gnt.locations.data.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.example.gnt.common.model.locations.LocationListItem
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.locations.data.LocationsRemoteMediator
import ru.example.gnt.locations.data.mapper.LocationEntityUiListMapper
import ru.example.gnt.locations.domain.repository.LocationListRepository
import ru.example.gnt.locations.presentation.list.LocationListFilterModel
import javax.inject.Inject


@ExperimentalPagingApi
class LocationListRepositoryImpl @Inject constructor(
    private val locationsDao: LocationsDao,
    private val locationEntityUiListMapper: LocationEntityUiListMapper,
    private val factory: LocationsRemoteMediator.LocationsRemoteMediatorFactory
) : LocationListRepository {
    override fun getLocationFilteredList(filter: LocationListFilterModel): Flow<PagingData<LocationListItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
            ),
            remoteMediator = getMediator(filter),
            pagingSourceFactory = {
                with(filter) {
                    locationsDao.getLocationsFilteredAndPaged(
                        name = name,
                        type = type,
                        dimension = dimension
                    )
                }
            }
        ).flow.map { it.map(locationEntityUiListMapper::mapTo) }

    }

    private fun getMediator(filter: LocationListFilterModel): LocationsRemoteMediator {
        return factory.create(filter)
    }

    companion object {
        private const val PAGE_SIZE = 6
    }

}
