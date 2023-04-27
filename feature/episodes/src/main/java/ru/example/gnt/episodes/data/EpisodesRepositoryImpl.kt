package ru.example.gnt.episodes.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.example.gnt.common.exceptions.DatabaseException
import ru.example.gnt.common.exceptions.NetworkException
import ru.example.gnt.common.isNetworkOn
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.common.utils.RetrofitResultWrapper
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.episodes.data.mapper.EpisodeEntityUiDetailsMapper
import ru.example.gnt.episodes.data.mapper.EpisodeResponseUiDetailsMapper
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.model.EpisodeFilterModel
import ru.example.gnt.episodes.domain.repository.EpisodesRepository
import javax.inject.Inject

@ExperimentalPagingApi
class EpisodesRepositoryImpl @Inject constructor(
    private val episodesDao: EpisodesDao,
    private val episodesService: EpisodeService,
    private val episodeEntityResponseMapper: EpisodeEntityResponseMapper,
    private val episodeResponseUiDetailsMapper: EpisodeResponseUiDetailsMapper,
    private val episodeEntityUiDetailsMapper: EpisodeEntityUiDetailsMapper,
    private val context: Context,
) : EpisodesRepository {
    override suspend fun getEpisodeListItemById(id: Int): Result<EpisodeDetailsItem> {
        var result =
            Result.failure<EpisodeDetailsItem>(NetworkException(resource = Resource.String(ru.example.gnt.ui.R.string.unknown_network_exception)))
        if (context.isNetworkOn()) {
            RetrofitResultWrapper(
                episodesService.getEpisodeById(id),
                episodeResponseUiDetailsMapper
            ) {
                result = it
            }
        } else {
            result = try {
                Result.success(episodeEntityUiDetailsMapper.mapTo(episodesDao.getEpisodeById(id) as EpisodeEntity))
            } catch (ex: Exception) {
                Result.failure(DatabaseException(resource = Resource.String(ru.example.gnt.ui.R.string.database_error)))
            }
        }
        return result
    }

    override fun getEpisodesFilteredList(filter: EpisodeFilterModel): Flow<PagingData<EpisodeListItem>> {
        TODO("Not yet implemented")
    }
}
