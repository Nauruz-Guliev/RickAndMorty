package ru.example.gnt.locations.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.local.entity.LocationEntity
import ru.example.gnt.data.remote.service.LocationService
import ru.example.gnt.locations.data.mapper.LocationResponseInfoEntityMapper
import ru.example.gnt.locations.presentation.list.LocationListFilterModel

@OptIn(ExperimentalPagingApi::class)
class LocationsRemoteMediator @AssistedInject constructor(
    private val locationsDao: LocationsDao,
    private val service: LocationService,
    @Assisted
    private val filterModel: LocationListFilterModel,
    private val responseInfoEntityMapper: LocationResponseInfoEntityMapper
) :
    RemoteMediator<Int, LocationEntity>() {

    private var limit = 0
        private set
    private var offset = 0
        private set
    private var pageIndex = 0


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocationEntity>
    ): MediatorResult = withContext(Dispatchers.IO) {
        pageIndex =
            getPageIndex(loadType) ?: return@withContext MediatorResult.Success(endOfPaginationReached = true)
        limit = state.config.pageSize
        offset = pageIndex * limit
        return@withContext try {
            val networkResult = with(filterModel) {
                service.getLocationsByPageFiltered(
                    page = pageIndex.toString(),
                    name = name,
                    type = type,
                    dimension = dimension
                )
            }.awaitResponse()
            if (networkResult.isSuccessful) {
                if (loadType == LoadType.REFRESH) {
                    with(filterModel) {
                        locationsDao.refresh(
                           locations = responseInfoEntityMapper.mapTo(networkResult.body()!!),
                            name = name,
                            type = type,
                            dimension = dimension
                        )
                    }
                } else {
                    locationsDao.saveLocations(responseInfoEntityMapper.mapTo(networkResult.body()!!))
                }
                MediatorResult.Success(endOfPaginationReached = (networkResult.body())!!.results.size < limit)
            } else {
                MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (ex: Exception) {
            MediatorResult.Error(ApplicationException.DataAccessException())
        }
    }

    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return null
            LoadType.APPEND -> ++pageIndex
        }
        return pageIndex
    }

    @AssistedFactory
    interface LocationsRemoteMediatorFactory {
        fun create(filterModel: LocationListFilterModel): LocationsRemoteMediator
    }
}
