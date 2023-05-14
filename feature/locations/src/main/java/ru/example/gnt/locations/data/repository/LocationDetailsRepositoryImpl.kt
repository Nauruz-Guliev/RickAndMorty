package ru.example.gnt.locations.data.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import ru.example.gnt.common.base.BaseMapper
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.model.Resource
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.common.utils.ApiListQueryGenerator
import ru.example.gnt.common.utils.UrlIdExtractor
import ru.example.gnt.common.utils.extensions.wrapRetrofitErrorRegular
import ru.example.gnt.data.di.qualifiers.RxIOSchedulerQualifier
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.local.dao.LocationsDao
import ru.example.gnt.data.mapper.CharacterEntityUiListItemMapper
import ru.example.gnt.data.mapper.CharacterResponseUiListItemMapper
import ru.example.gnt.data.mapper.LocationEntityResponseMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.LocationService
import ru.example.gnt.locations.data.mapper.LocationEntityUiDetailsMapper
import ru.example.gnt.locations.data.mapper.LocationResponseUiDetailsMapper
import ru.example.gnt.locations.domain.repository.LocationDetailsRepository
import ru.example.gnt.locations.presentation.details.LocationDetailsModel
import javax.inject.Inject

class LocationDetailsRepositoryImpl @Inject constructor(
    //dao
    private val locationsDao: LocationsDao,
    private val characterDao: CharactersDao,
    //service
    private val locationService: LocationService,
    private val characterService: CharacterService,
    //mappers
    private val locationEntityUiDetailsMapper: LocationEntityUiDetailsMapper,
    private val locationResponseUiDetailsMapper: LocationResponseUiDetailsMapper,
    private val locationEntityResponseMapper: LocationEntityResponseMapper,
    private val characterResponseUiListItemMapper: CharacterResponseUiListItemMapper,
    private val characterEntityUiListItemMapper: CharacterEntityUiListItemMapper,
    //utility
    private val urlIdExtractor: UrlIdExtractor,
    private val apiListQueryGenerator: ApiListQueryGenerator,
    @RxIOSchedulerQualifier private val scheduler: Scheduler
) : LocationDetailsRepository {
    override fun getLocationDetailsItemById(id: Int): Observable<LocationDetailsModel> {
        return Observable.create { emitter ->
            val locationLocal = loadLocal(id)
            if (locationLocal != null) emitter.onNext(locationLocal)
            try {
                val locationRemote = locationService.getLocation(id).execute().body()
                if(locationRemote !=null) locationsDao.save(locationEntityResponseMapper.mapTo(locationRemote))
                if (locationRemote != null && locationRemote.residents?.isEmpty() == false) {
                    val locationRemoteUpdated = loadMissingValues(
                        ids = locationRemote.residents?.map(urlIdExtractor::extract),
                        mapper = locationResponseUiDetailsMapper,
                        location = locationRemote
                    )
                    if (locationRemoteUpdated != null) emitter.onNext(locationRemoteUpdated)
                } else {
                    if (locationRemote != null) emitter.onNext(
                        locationResponseUiDetailsMapper.mapTo(
                            locationRemote
                        )
                    )
                }
                emitter.onComplete()
            } catch (ex: Exception) {
                if (locationLocal != null) emitter.onError(
                    ApplicationException.LocalDataException(
                        data = locationLocal,
                        cause = ex
                    )
                ) else emitter.onError(
                    ApplicationException.DataAccessException(
                        ex,
                        Resource.String(ru.example.gnt.ui.R.string.data_access_error)
                    )
                )
                emitter.onComplete()
            }
        }
    }

    private fun loadLocal(id: Int): LocationDetailsModel? {
        val location = locationsDao.getLocationById(id).blockingGet() ?: return null
        return try {
            loadMissingValues(
                ids = location.residents,
                mapper = locationEntityUiDetailsMapper,
                location = location
            )
        } catch (ex: Exception) {
            locationEntityUiDetailsMapper.mapTo(location)
        }
    }

    private fun <T> loadMissingValues(
        ids: List<String>?,
        location: T?,
        mapper: BaseMapper<T, LocationDetailsModel>
    ): LocationDetailsModel? {
        return if (location == null || ids == null || ids.isEmpty()) null
        else mapper.mapTo(location).apply {
            residents = getCharacterList(ids)
        }
    }

    private fun getCharacterList(ids: List<String>?): List<CharacterListItem>? {
        if (ids == null) return listOf()
        return try {
            if (ids.size == 1) {
                wrapRetrofitErrorRegular {
                    listOf(
                        characterResponseUiListItemMapper.mapTo(
                            characterService.getCharacterById(
                                Integer.valueOf(ids[0])
                            ).blockingGet()
                        )
                    )
                }
            } else {
                wrapRetrofitErrorRegular {
                    characterService.getCharactersInRange(apiListQueryGenerator.generate(ids))
                        .execute().body()?.map(characterResponseUiListItemMapper::mapTo)
                }
            }
        } catch (ex: Exception) {
            characterDao.getCharacters(ids).subscribeOn(scheduler).blockingGet()
                ?.map(characterEntityUiListItemMapper::mapTo)
        }
    }
}
