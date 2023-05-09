package ru.example.gnt.episodes.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import ru.example.gnt.common.di.qualifiers.IoDispatcher
import ru.example.gnt.common.exceptions.DataAccessException
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.common.utils.ApiListQueryGenerator
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.common.utils.extensions.networkBoundResource
import ru.example.gnt.common.utils.extensions.wrapRetrofitError
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.entity.EpisodeEntity
import ru.example.gnt.data.mapper.CharacterEntityUiListItemMapper
import ru.example.gnt.data.mapper.CharacterResponseUiListItemMapper
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.episodes.R
import ru.example.gnt.episodes.data.mapper.EpisodeEntityUiDetailsMapper
import ru.example.gnt.episodes.domain.model.EpisodeDetailsItem
import ru.example.gnt.episodes.domain.repository.EpisodeDetailsRepository
import java.io.IOException
import javax.inject.Inject

class EpisodeDetailsRepositoryImpl @Inject constructor(
    //dao
    private val characterDao: CharactersDao,
    private val episodesDao: EpisodesDao,
    //service
    private val episodesService: EpisodeService,
    private val characterService: CharacterService,
    //mapper
    private val entityUiDetailsMapper: EpisodeEntityUiDetailsMapper,
    private val episodeEntityResponseMapper: EpisodeEntityResponseMapper,
    private val characterResponseUiListItemMapper: CharacterResponseUiListItemMapper,
    private val characterEntityUiListItemMapper: CharacterEntityUiListItemMapper,
    //utility
    private val urlIdExtractor: UrlIdExtractor,
    private val queryGenerator: ApiListQueryGenerator,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : EpisodeDetailsRepository {
    override suspend fun getEpisodeDetailsItemById(id: Int): Result<EpisodeDetailsItem> =
        withContext(coroutineDispatcher) {
            return@withContext try {
                networkBoundResource(
                    query = {
                        flowOf(entityUiDetailsMapper.mapTo(episodesDao.getEpisodeById(id) as EpisodeEntity))
                    },
                    fetch = {
                        wrapRetrofitError { episodesService.getEpisodeById(id) }
                    },
                    saveFetchResult = {
                        episodesDao.save(episodeEntityResponseMapper.mapTo(it))
                    },
                    transformResult = { resultType, requestType ->
                        resultType.apply {
                            characters =
                                getCharacters(requestType.characters?.map(urlIdExtractor::extract))
                        }
                    }
                ).first()
            } catch (ex: Exception) {
                Result.failure(DataAccessException(resource = Resource.String(R.string.unknown_data_access_error)))
            }
        }

    private suspend fun getCharacters(ids: List<String>?): List<CharacterListItem>? {
        return try {
            if (ids == null) return null
            if (ids.size == 1) {
                listOf(
                    characterResponseUiListItemMapper.mapTo(
                        characterService.getCharacterById(
                            Integer.valueOf(ids[0])
                        ).blockingGet()
                    )
                )
            } else {
                characterService.getCharactersInRange(queryGenerator.generate(ids)).awaitResponse()
                    .body()!!.map(characterResponseUiListItemMapper::mapTo)
            }
        } catch (ex: IOException) {
            characterDao.getCharacters(ids).map(characterEntityUiListItemMapper::mapTo)
        } catch (ex: Exception) {
            throw DataAccessException(
                cause = ex,
                resource = Resource.String(R.string.unknown_data_access_error)
            )
        }
    }
}
