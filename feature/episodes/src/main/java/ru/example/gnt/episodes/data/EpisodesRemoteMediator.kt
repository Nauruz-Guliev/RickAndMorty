package ru.example.gnt.episodes.data

import android.content.Context
import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import ru.example.gnt.common.exceptions.DataAccessException
import ru.example.gnt.common.exceptions.DatabaseException
import ru.example.gnt.common.isNetworkOn
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.utils.RetrofitCachingResultWrapper
import ru.example.gnt.common.utils.RetrofitResult
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.episodes.data.mapper.EpisodeResponseInfoEntityMapper
import ru.example.gnt.episodes.domain.model.EpisodeFilterModel
import ru.example.gnt.ui.R

@OptIn(ExperimentalPagingApi::class)
class EpisodesRemoteMediator @AssistedInject constructor(
    private val episodesDao: EpisodesDao,
    private val episodesService: EpisodeService,
    private val context: Context,
    @Assisted
    private val filterModel: EpisodeFilterModel,
    private val responseInfoEntityMapper: EpisodeResponseInfoEntityMapper
) : RemoteMediator<Int, EpisodeEntity>() {

    private var pageIndex = 0

    var limit = 0
        private set
    var offset = 0
        private set


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EpisodeEntity>
    ): MediatorResult {
        pageIndex =
            getPageIndex(loadType) ?: return MediatorResult.Success(endOfPaginationReached = true)
        limit = state.config.pageSize
        offset = pageIndex * limit

        return when (val retrofitResult = fetchEpisodes(limit = limit, offset = offset)) {
            is RetrofitResult.Error -> {
                Log.d("RETROFIT_RESULT", "ERROR")
                MediatorResult.Error(retrofitResult.message)
            }
            is RetrofitResult.Success -> {
                if (loadType == LoadType.REFRESH) {
                    with(filterModel) {
                        episodesDao.refresh(
                            episodes = retrofitResult.data,
                            name = name,
                            episode = episode
                        )
                    }
                } else {
                    episodesDao.saveEpisodes(retrofitResult.data)
                }
                MediatorResult.Success(
                    endOfPaginationReached = retrofitResult.data.size < limit
                )
            }
            else -> {
                MediatorResult.Error(
                    DataAccessException(resource = Resource.String(ru.example.gnt.episodes.R.string.unknown_data_access_error))
                )
            }
        }
    }

    private suspend fun fetchEpisodes(
        limit: Int,
        offset: Int
    ): RetrofitResult<List<EpisodeEntity>>? {
        return RetrofitCachingResultWrapper.builder<EpisodesResponseModel, List<EpisodeEntity>, EpisodesResponseModel>()
            .retrofitCall(
                episodesService.getEpisodesByPageFiltered(
                    page = pageIndex.toString(),
                    name = filterModel.name,
                    episode = filterModel.episode
                )
            )
            .retrofitResponseMapper(responseInfoEntityMapper)
            .isNetworkOn(context.isNetworkOn())
            .cacheSource {
                try {
                    Result.success(
                        episodesDao.getEpisodesFiltered(
                            name = filterModel.name,
                            episode = filterModel.episode,
                            limit = limit,
                            offset = offset
                        )
                    ).map(responseInfoEntityMapper::mapFrom)
                } catch (ex: Exception) {
                    Result.failure(DatabaseException(resource = Resource.String(R.string.database_error)))
                }
            }
            .entityMapper(responseInfoEntityMapper)
            .build()
            .getEntityResult().filter { it !is RetrofitResult.Empty }.first()
    }

    @AssistedFactory
    interface EpisodesRemoteMediatorFactory {
        @OptIn(ExperimentalPagingApi::class)
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
