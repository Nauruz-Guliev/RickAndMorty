package ru.example.gnt.episodes.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import ru.example.gnt.common.di.qualifiers.IoDispatcher
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.utils.extensions.wrapRetrofitErrorSuspending
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.episodes.data.mapper.EpisodeResponseInfoEntityMapper
import ru.example.gnt.episodes.domain.model.EpisodeFilterModel

@OptIn(ExperimentalPagingApi::class)
class EpisodesRemoteMediator @AssistedInject constructor(
    private val episodesDao: EpisodesDao,
    private val episodesService: EpisodeService,
    @Assisted
    private val filterModel: EpisodeFilterModel,
    private val responseInfoEntityMapper: EpisodeResponseInfoEntityMapper,

    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : RemoteMediator<Int, EpisodeEntity>() {

    private var pageIndex = 0
    private var limit = 0
    private var offset = 0
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EpisodeEntity>
    ): MediatorResult = withContext(dispatcher) {
        pageIndex =
            getPageIndex(loadType) ?: return@withContext MediatorResult.Success(
                endOfPaginationReached = true
            )
        limit = state.config.pageSize
        offset = pageIndex * limit
        return@withContext try {
            val networkResult = wrapRetrofitErrorSuspending {
                with(filterModel) {
                    episodesService.getEpisodesByPageFiltered(
                        page = pageIndex.toString(),
                        name = name,
                        episode = episode
                    )
                }
            }
            if (loadType == LoadType.REFRESH) {
                with(filterModel) {
                    episodesDao.refresh(
                        episodes = responseInfoEntityMapper.mapTo(networkResult),
                        name = name,
                        episode = episode
                    )
                }
            } else {
                episodesDao.saveEpisodes(responseInfoEntityMapper.mapTo(networkResult))
            }
            MediatorResult.Success(endOfPaginationReached = (networkResult).results.size < limit)
        } catch (ex: ApplicationException.BackendException) {
            if (ex.code == 404) {
                MediatorResult.Success(endOfPaginationReached = true)
            } else {
                MediatorResult.Error(ex)
            }
        } catch (ex: ApplicationException) {
            MediatorResult.Error(ex)
        } catch (ex: Exception) {
            MediatorResult.Error(
                ApplicationException.DataAccessException(
                    resource = Resource.String(
                        ru.example.gnt.ui.R.string.data_access_error
                    )
                )
            )
        }
    }


    @AssistedFactory
    interface EpisodesRemoteMediatorFactory {
        fun create(filterModel: EpisodeFilterModel): EpisodesRemoteMediator
    }

    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return null
            LoadType.APPEND -> ++pageIndex
        }
        return pageIndex
    }
}
