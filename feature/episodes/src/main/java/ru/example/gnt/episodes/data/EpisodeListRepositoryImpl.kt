package ru.example.gnt.episodes.data

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.example.gnt.common.exceptions.DataAccessException
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.common.utils.DataResource
import ru.example.gnt.common.utils.extensions.networkResource
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.episodes.R
import ru.example.gnt.episodes.data.mapper.EpisodeEntityUiDetailsMapper
import ru.example.gnt.episodes.data.mapper.EpisodeEntityUiListMapper
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.model.EpisodeFilterModel
import ru.example.gnt.episodes.domain.repository.EpisodeListRepository
import javax.inject.Inject

@ExperimentalPagingApi
class EpisodeListRepositoryImpl @Inject constructor(
    private val episodesDao: EpisodesDao,
    private val episodeEntityListMapper: EpisodeEntityUiListMapper,
    private val factory: EpisodesRemoteMediator.EpisodesRemoteMediatorFactory,
) : EpisodeListRepository {
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
            .map { pagingData ->
                pagingData.map {
                    episodeEntityListMapper.mapTo(it)
                }
            }
    }

    private fun getMediator(filter: EpisodeFilterModel): EpisodesRemoteMediator {
        return factory.create(filter)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
