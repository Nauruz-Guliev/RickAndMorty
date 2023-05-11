package ru.example.gnt.locations.data.repository

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import ru.example.gnt.common.R
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
import java.io.IOException
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
    override fun getLocationDetailsItemById(id: Int): Single<LocationDetailsModel> {
        return locationService.getLocationById(id).map { locationsResponse ->
            locationResponseUiDetailsMapper.mapTo(locationsResponse).apply {
                residents =
                    getCharacterList(locationsResponse.residents?.map(urlIdExtractor::extract))
                locationsDao.saveLocationBlocking(
                    listOf(
                        locationEntityResponseMapper.mapTo(
                            locationsResponse
                        )
                    )
                )
            }
        }.onErrorReturn { exception ->
            when (exception) {
                is IOException -> {
                    val location = locationsDao.getLocationById(id).blockingGet()
                    locationEntityUiDetailsMapper.mapTo(
                        locationsDao.getLocationById(id).blockingGet()
                    ).apply {
                        residents = getCharacterList(location.residents ?: listOf())
                    }
                }
                is ApplicationException -> throw exception
                else -> {
                    throw ApplicationException.DataAccessException(
                        exception,
                        Resource.String(R.string.data_access_error)
                    )
                }
            }
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
        } catch (ex: IOException) {
            characterDao.getCharacters(ids).subscribeOn(scheduler).blockingGet()?.map(characterEntityUiListItemMapper::mapTo)
        }
    }
}
