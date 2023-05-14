package ru.example.gnt.locations.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.example.gnt.common.di.qualifiers.IoDispatcher
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.utils.extensions.wrapRetrofitErrorSuspending
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.local.entity.LocationEntity
import ru.example.gnt.data.remote.service.LocationService
import ru.example.gnt.locations.data.mapper.LocationResponseEntityMapper
import ru.example.gnt.locations.presentation.list.LocationListFilterModel

@OptIn(ExperimentalPagingApi::class)
class LocationsRemoteMediator @AssistedInject constructor(
    private val locationsDao: LocationsDao,
    private val service: LocationService,
    @Assisted
    private val filterModel: LocationListFilterModel,
    private val responseInfoEntityMapper: LocationResponseEntityMapper,

    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) :
    RemoteMediator<Int, LocationEntity>() {

    private var limit = 0
    private var offset = 0
    private var pageIndex = 0


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocationEntity>
    ): MediatorResult = withContext(ioDispatcher) {
        pageIndex =
            getPageIndex(loadType) ?: return@withContext MediatorResult.Success(
                endOfPaginationReached = true
            )
        limit = state.config.pageSize
        offset = pageIndex * limit
        return@withContext try {
            val networkResult = wrapRetrofitErrorSuspending {
                with(filterModel) {
                    service.getLocationsByPageFiltered(
                        page = pageIndex.toString(),
                        name = name,
                        type = type,
                        dimension = dimension
                    )
                }
            }
            if (loadType == LoadType.REFRESH) {
                with(filterModel) {
                    locationsDao.refresh(
                        locations = responseInfoEntityMapper.mapTo(networkResult),
                        name = name,
                        type = type,
                        dimension = dimension
                    )
                }
            } else {
                locationsDao.saveLocations(responseInfoEntityMapper.mapTo(networkResult))
            }
            MediatorResult.Success(endOfPaginationReached = networkResult.results.size < limit)
        } catch (ex: ApplicationException.BackendException) {
            if (ex.code == 404) {
                MediatorResult.Success(true)
            } else {
                MediatorResult.Error(ex)
            }
        } catch (ex: ApplicationException) {
            MediatorResult.Error(ex)
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
