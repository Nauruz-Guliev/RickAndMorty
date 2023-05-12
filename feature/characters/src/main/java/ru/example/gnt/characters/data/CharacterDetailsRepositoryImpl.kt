package ru.example.gnt.characters.data

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import ru.example.gnt.characters.data.mapper.CharacterEntityUiDetailsMapper
import ru.example.gnt.characters.data.mapper.CharacterResponseUiDetailsMapper
import ru.example.gnt.characters.domain.repository.CharacterDetailsRepository
import ru.example.gnt.characters.presentation.detials.CharacterDetailsModel
import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.common.model.locations.LocationListItem
import ru.example.gnt.common.utils.ApiListQueryGenerator
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.common.utils.extensions.wrapRetrofitErrorRegular
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.local.dao.EpisodesDao
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.mapper.EpisodeEntityUiListMapper
import ru.example.gnt.data.mapper.EpisodeResponseUiListItemMapper
import ru.example.gnt.data.mapper.LocationEntityUiListMapper
import ru.example.gnt.data.mapper.LocationResponseUiListItemMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.EpisodeService
import ru.example.gnt.data.remote.service.LocationService
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CharacterDetailsRepositoryImpl @Inject constructor(
    //dao
    private val charactersDao: CharactersDao,
    private val episodeDao: EpisodesDao,
    private val locationsDao: LocationsDao,
    //service
    private val characterService: CharacterService,
    private val episodeService: EpisodeService,
    private val locationService: LocationService,
    //mappers
    private val episodeUiListItemMapper: EpisodeResponseUiListItemMapper,
    private val episodeEntityUiListMapper: EpisodeEntityUiListMapper,
    private val characterResponseUiDetailsMapper: CharacterResponseUiDetailsMapper,
    private val characterEntityUiDetailsMapper: CharacterEntityUiDetailsMapper,
    private val locationResponseUiListItemMapper: LocationResponseUiListItemMapper,
    private val locationEntityUiListMapper: LocationEntityUiListMapper,
    //utility
    private val urlIdExtractor: UrlIdExtractor,
    private val apiListQueryGenerator: ApiListQueryGenerator,
) : CharacterDetailsRepository {

    override fun getCharacterById(id: Int): Observable<CharacterDetailsModel> {
        return Observable.create<CharacterDetailsModel> { emitter ->
            val characterLocal = loadLocal(id)
            Log.d("CHARACTER_LOCAL", "LOCAL" + characterLocal)
            if (characterLocal != null) emitter.onNext(characterLocal)

            val characterRemote = characterService.getCharacterById(id).blockingGet()
            loadMissingValues(
                ids = characterRemote.episode?.map(urlIdExtractor::extract),
                character = characterRemote,
                locationId = urlIdExtractor.extract(characterRemote.location?.url),
                originId = urlIdExtractor.extract(characterRemote.origin?.url),
                mapper = characterResponseUiDetailsMapper
            )

        }.delay(100, TimeUnit.MILLISECONDS)
            .onErrorReturn { exception ->
                Log.e(
                    "CHARACTER_LOCAL",
                    exception.message.toString() + exception.javaClass.name.toString()
                )
                when (exception) {
                    is IOException, is RuntimeException -> {
                        Log.e("CHARACTER_LOCAL_NEW", loadLocal(id).toString())
                        throw ApplicationException.LocalDataException(resource = Resource.String(ru.example.gnt.ui.R.string.local_data_warning))
                    }
                    is ApplicationException -> throw exception
                    else -> {
                        throw ApplicationException.DataAccessException(
                            exception,
                            Resource.String(ru.example.gnt.ui.R.string.data_access_error)
                        )
                    }
                }
            }
    }


    private fun loadLocal(id: Int): CharacterDetailsModel? {
        val character = charactersDao.getCharacterById(id) ?: return null
        return try {
            with(character) {
                loadMissingValues(
                    ids = episode,
                    character = character,
                    locationId = character.locationId,
                    originId = character.originId,
                    mapper = characterEntityUiDetailsMapper
                )
            }
        } catch (ex: Exception) {
            characterEntityUiDetailsMapper.mapTo(character)
        }
    }


    private fun <T> loadMissingValues(
        ids: List<String>?,
        character: T,
        locationId: String?,
        originId: String?,
        mapper: BaseMapper<T, CharacterDetailsModel>
    ): CharacterDetailsModel {
        return mapper.mapTo(character).apply {
            episode = if (ids != null) getEpisodeList(ids) else listOf()
            this.location = getLocationById(locationId)
            this.origin = getLocationById(originId)
        }
    }


    override fun getEpisodeList(ids: List<String>): List<EpisodeListItem> {
        return try {
            if (ids.size > 1) {
                wrapRetrofitErrorRegular {
                    episodeService.getEpisodesInRange(apiListQueryGenerator.generate(ids))
                        .execute().body()
                        ?.map(episodeUiListItemMapper::mapTo) ?: listOf()
                }
            } else {
                return wrapRetrofitErrorRegular {
                    val episode =
                        episodeService.getEpisodeById(Integer.valueOf(ids[0])).execute().body()
                    if (episode != null) {
                        listOf(episodeUiListItemMapper.mapTo(episode))
                    } else {
                        listOf()
                    }
                }
            }
        } catch (ex: Exception) {
            episodeDao.getEpisodes(ids).map(episodeEntityUiListMapper::mapTo)
        }
    }

    private fun getLocationById(id: String?): LocationListItem? {
        if (id == "" || id == null) return null
        return try {
            return wrapRetrofitErrorRegular {
                val location = locationService.getLocation(Integer.valueOf(id)).execute().body()
                if (location != null) {
                    locationResponseUiListItemMapper.mapTo(location)
                } else {
                    null
                }
            }
        } catch (ex: Exception) {
            val result =
                locationsDao.getLocations(listOf(id)).map(locationEntityUiListMapper::mapTo)
            if (result.isNotEmpty()) return result[0] else null
        }
    }
}
