package ru.example.gnt.episodes.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import ru.example.gnt.common.exceptions.ApplicationException
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
    ): MediatorResult = withContext(Dispatchers.IO){
        pageIndex =
            getPageIndex(loadType) ?: return@withContext MediatorResult.Success(endOfPaginationReached = true)
        limit = state.config.pageSize
        offset = pageIndex * limit
        return@withContext try {
            val networkResult = with(filterModel) {
                episodesService.getEpisodesByPageFiltered(
                    page = pageIndex.toString(),
                    name = name,
                    episode = episode
                )
            }.awaitResponse()
            if (networkResult.isSuccessful) {
                if (loadType == LoadType.REFRESH) {
                    with(filterModel) {
                        episodesDao.refresh(
                            episodes = responseInfoEntityMapper.mapTo(networkResult.body()!!),
                            name = name,
                            episode = episode
                        )
                    }
                } else {
                    episodesDao.saveEpisodes(responseInfoEntityMapper.mapTo(networkResult.body()!!))
                }
                MediatorResult.Success(endOfPaginationReached = (networkResult.body())!!.results.size < limit)
            } else {
                MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (ex : ApplicationException.ConnectionException) {
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (ex: Exception) {
            MediatorResult.Error(ApplicationException.DataAccessException())
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
