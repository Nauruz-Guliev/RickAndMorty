package ru.example.gnt.episodes.data

import io.reactivex.rxjava3.core.Scheduler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import ru.example.gnt.common.di.qualifiers.IoDispatcher
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.common.utils.ApiListQueryGenerator
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.common.utils.extensions.networkBoundResource
import ru.example.gnt.common.utils.extensions.wrapRetrofitErrorRegular
import ru.example.gnt.common.utils.extensions.wrapRetrofitErrorSuspending
import ru.example.gnt.data.di.qualifiers.RxIOSchedulerQualifier
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.mapper.CharacterEntityUiListItemMapper
import ru.example.gnt.data.mapper.CharacterResponseUiListItemMapper
import ru.example.gnt.data.mapper.EpisodeEntityResponseMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.EpisodeService
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
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    @RxIOSchedulerQualifier private val scheduler: Scheduler
) : EpisodeDetailsRepository {
    override suspend fun getEpisodeDetailsItemById(id: Int): Flow<Result<EpisodeDetailsItem>> =
        withContext(coroutineDispatcher) {
            networkBoundResource(
                query = {
                    flowOf(episodesDao.getEpisodeById(id))
                },
                fetch = {
                    episodesService.getEpisodeById(id)
                },
                saveFetchResult = {
                    episodesDao.save(episodeEntityResponseMapper.mapTo(it))
                }
            ).map { result ->
                result.map { episodeEntity ->
                    if (episodeEntity == null) throw ApplicationException.EmptyResultException(
                        resource = Resource.String(
                            ru.example.gnt.ui.R.string.no_data_available_error
                        )
                    )
                    entityUiDetailsMapper.mapTo(episodeEntity).apply {
                        characters = getCharacters(episodeEntity.characters)
                    }
                }
            }
        }

    private suspend fun getCharacters(ids: List<String>?): List<CharacterListItem>? {
        return try {
            if (ids == null) return null
            if (ids.size == 1) {
                listOf(
                    wrapRetrofitErrorRegular {
                        characterResponseUiListItemMapper.mapTo(
                            characterService.getCharacterById(
                                Integer.valueOf(ids[0])
                            ).blockingGet()
                        )
                    }
                )
            } else {
                wrapRetrofitErrorSuspending {
                    characterService.getCharactersInRange(queryGenerator.generate(ids))
                        .awaitResponse().body()?.map(characterResponseUiListItemMapper::mapTo)
                }
            }
        } catch (ex: IOException) {
            characterDao.getCharacters(ids).subscribeOn(scheduler).blockingGet()?.map(characterEntityUiListItemMapper::mapTo)
        } catch (ex: Exception) {
            throw ApplicationException.DataAccessException(
                cause = ex,
                resource = Resource.String(ru.example.gnt.ui.R.string.unknown_data_access_error)
            )
        }
    }
}
