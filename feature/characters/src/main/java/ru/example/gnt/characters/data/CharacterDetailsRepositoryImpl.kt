package ru.example.gnt.characters.data

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import ru.example.gnt.characters.data.mapper.CharacterEntityUiDetailsMapper
import ru.example.gnt.characters.data.mapper.CharacterResponseUiDetailsMapper
import ru.example.gnt.characters.domain.repository.CharacterDetailsRepository
import ru.example.gnt.characters.presentation.detials.CharacterDetailsModel
import ru.example.gnt.common.exceptions.ConnectionException
import ru.example.gnt.common.exceptions.DataAccessException
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.common.model.locations.LocationListItem
import ru.example.gnt.common.utils.ApiListQueryGenerator
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.data.di.qualifiers.RxIOSchedulerQualifier
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.mapper.EpisodeResponseUiListItemMapper
import ru.example.gnt.data.mapper.LocationResponseUiListItemMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.data.remote.service.LocationService
import javax.inject.Inject

class CharacterDetailsRepositoryImpl @Inject constructor(
    private val charactersDao: CharactersDao,
    private val characterService: CharacterService,
    private val episodeDao: EpisodesDao,
    private val episodeService: EpisodeService,
    private val locationService: LocationService,
    private val locationsDao: LocationsDao,
    private val episodeUiListItemMapper: EpisodeResponseUiListItemMapper,
    private val characterResponseUiDetailsMapper: CharacterResponseUiDetailsMapper,
    private val characterEntityUiDetailsMapper: CharacterEntityUiDetailsMapper,
    private val urlIdExtractor: UrlIdExtractor,
    private val apiListQueryGenerator: ApiListQueryGenerator,
    private val locationResponseUiListItemMapper: LocationResponseUiListItemMapper,
) : CharacterDetailsRepository {

    override fun getCharacterById(id: Int): Single<CharacterDetailsModel> {
        return try {
            val result = characterService.getCharacterById(id).map { characterResponse ->
                val episodeIds = characterResponse.episode?.map(urlIdExtractor::extract)
                characterResponseUiDetailsMapper.mapTo(characterResponse).apply {
                    episode = if (episodeIds != null) getEpisodeList(episodeIds) else listOf()
                    location =
                        getLocationById(urlIdExtractor.extract(characterResponse.location?.url))
                    origin = getLocationById(urlIdExtractor.extract(characterResponse.origin?.url))
                }
            }
            result
        } catch (ex: ConnectionException) {
            charactersDao.getCharacterById(id).map(characterEntityUiDetailsMapper::mapTo)
        } catch (ex: Exception) {
            Single.error { DataAccessException(resource = Resource.String(ru.example.gnt.common.R.string.data_access_error)) }
        }
    }

    override fun getEpisodeList(ids: List<String>): List<EpisodeListItem> {
        return if (ids.size > 1) {
            episodeService.getEpisodesInRange(apiListQueryGenerator.generate(ids))
                .execute().body()
                ?.map(episodeUiListItemMapper::mapTo) ?: listOf()
        } else {
            listOf(
                episodeUiListItemMapper.mapTo(
                    episodeService.getEpisodeById(
                        Integer.valueOf(
                            ids[0]
                        )
                    ).execute().body()!!
                )
            )
        }
    }

    private fun getLocationById(id: String?): LocationListItem? {
        if (id == "" || id == null) return null
        return locationResponseUiListItemMapper.mapTo(
            locationService.getLocationById(
                Integer.valueOf(
                    id
                )
            ).execute().body()!!
        )
    }
}
