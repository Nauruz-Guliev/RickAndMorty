package ru.example.gnt.episodes.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import ru.example.gnt.common.exceptions.DataAccessException
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.utils.DataResource
import ru.example.gnt.common.utils.extensions.networkResource
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.episodes.R
import ru.example.gnt.episodes.data.mapper.EpisodeEntityUiDetailsMapper
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.repository.EpisodeDetailsRepository
import javax.inject.Inject

class EpisodeDetailsRepositoryImpl @Inject constructor(
    private val entityUiDetailsMapper: EpisodeEntityUiDetailsMapper,
    private val episodesDao: EpisodesDao,
    private val episodeEntityResponseMapper: EpisodeEntityResponseMapper,
    private val episodesService: EpisodeService,
) : EpisodeDetailsRepository {

    override suspend fun getEpisodeListItemById(id: Int): DataResource<EpisodeDetailsItem> = withContext(Dispatchers.IO){
        return@withContext try {
            networkResource(
                query = {
                    flowOf(entityUiDetailsMapper.mapTo(episodesDao.getEpisodeById(id) as EpisodeEntity))
                },
                fetch = {
                    episodesService.getEpisodeById(id)
                },
                saveFetchResult = {
                    episodesDao.save(episodeEntityResponseMapper.mapTo(it))
                }
            ).first()
        } catch (ex: Exception) {
            Log.d("ERROR", ex.message.toString())
            DataResource.Error(
                message = DataAccessException(resource = Resource.String(R.string.unknown_data_access_error)),
                data = null
            )
        }
    }
}
