package ru.example.gnt.episodes.data

import android.content.Context
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.example.gnt.common.exceptions.DatabaseException
import ru.example.gnt.common.isNetworkOn
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.common.utils.RetrofitCachingResultWrapper
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.remote.model.EpisodesResponseModel
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.episodes.data.mapper.EpisodeEntityUiDetailsMapper
import ru.example.gnt.episodes.data.mapper.EpisodeEntityUiListMapper
import ru.example.gnt.episodes.data.mapper.EpisodeResponseUiDetailsMapper
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.model.EpisodeFilterModel
import ru.example.gnt.episodes.domain.repository.EpisodesRepository
import javax.inject.Inject

@ExperimentalPagingApi
class EpisodesRepositoryImpl @Inject constructor(
    private val episodesDao: EpisodesDao,
    private val episodesService: EpisodeService,
    private val episodeResponseUiDetailsMapper: EpisodeResponseUiDetailsMapper,
    private val episodeEntityUiDetailsMapper: EpisodeEntityUiDetailsMapper,
    private val episodeEntityListMapper: EpisodeEntityUiListMapper,
    private val factory: EpisodesRemoteMediator.EpisodesRemoteMediatorFactory,
    private val context: Context,
) : EpisodesRepository {
    override suspend fun getEpisodeListItemById(id: Int): Result<EpisodeDetailsItem> {
        return RetrofitCachingResultWrapper.builder<EpisodesResponseModel.Result, EpisodeDetailsItem, EpisodeEntity>()
            .retrofitCall(episodesService.getEpisodeById(id))
            .retrofitResponseMapper(episodeResponseUiDetailsMapper)
            .isNetworkOn(context.isNetworkOn())
            .cacheSource {
                try {
                    Result.success(episodesDao.getEpisodeById(id))
                } catch (ex: Exception) {
                    Result.failure(DatabaseException(resource = Resource.String(ru.example.gnt.ui.R.string.database_error)))
                }
            }
            .entityMapper(episodeEntityUiDetailsMapper)
            .build()
            .getUiResult()
    }

    override fun getEpisodesFilteredList(filter: EpisodeFilterModel): Flow<PagingData<EpisodeListItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
            ),
            remoteMediator = getMediator(filter),
            pagingSourceFactory = {
                episodesDao.getEpisodesFilteredAndPaged(
                    name = filter.name,
                    episode = filter.episode
                )
            }
        )
            .flow
            .map { pagingData -> pagingData.map(episodeEntityListMapper::mapTo) }
    }

    private fun getMediator(filter: EpisodeFilterModel): EpisodesRemoteMediator {
        return factory.create(filter)
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}
