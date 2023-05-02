package ru.example.gnt.locations.data.repository

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.flowOf
import ru.example.gnt.common.utils.extensions.networkBoundResource
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.local.entity.LocationEntity
import ru.example.gnt.data.mapper.LocationEntityResponseMapper
import ru.example.gnt.data.remote.service.LocationService
import ru.example.gnt.locations.data.mapper.LocationEntityUiDetailsMapper
import ru.example.gnt.locations.domain.repository.LocationDetailsRepository
import ru.example.gnt.locations.presentation.details.LocationDetailsModel
import javax.inject.Inject

class LocationDetailsRepositoryImpl @Inject constructor(
    private val locationEntityUiDetailsMapper: LocationEntityUiDetailsMapper,
    private val locationsDao: LocationsDao,
    private val locationEntityResponseMapper: LocationEntityResponseMapper,
    private val locationService: LocationService
) : LocationDetailsRepository {
    override fun getLocationDetailsItemById(id: Int): Result<LocationDetailsModel> {
        return try {
            networkBoundResource(
                query = {
                    flowOf(locationEntityUiDetailsMapper.mapTo(locationsDao.getLocationById(id) as LocationEntity))
                },
                fetch = {
                    locationService.getLocationById(id)
                },
                saveFetchResult = {
                    locationsDao.save(locationEntityResponseMapper.mapTo(it))
                }
            ).asLiveData().value!!
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}
