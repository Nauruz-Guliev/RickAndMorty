package ru.example.gnt.episodes.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import ru.example.gnt.common.utils.RetrofitCachingResultWrapper
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.episodes.data.mapper.EpisodeEntityUiListMapper
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.model.EpisodeFilterModel
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class EpisodesRemoteMediator @Inject constructor(
    private val episodesDao: EpisodesDao,
    private val episodesService: EpisodeService,
    private val context: Context,
    @Assisted
    private val filterModel: EpisodeFilterModel,
    private val mapper: EpisodeEntityUiListMapper
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

        fetchEpisodes(limit = limit, offset = offset)
    }

    private fun fetchEpisodes(limit: Int, offset: Int) {
        RetrofitCachingResultWrapper.builder<EpisodesResponseModel, EpisodeDetailsItem, EpisodeEntity>()
            .retrofitCall(
                episodesService.getEpisodesByPageFiltered(
                    page = pageIndex.toString(),
                    name = filterModel.name,
                    episode = filterModel.episode
                )
            )
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