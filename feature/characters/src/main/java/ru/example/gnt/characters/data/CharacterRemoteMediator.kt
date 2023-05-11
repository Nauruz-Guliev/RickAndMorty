package ru.example.gnt.characters.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.*
import ru.example.gnt.characters.presentation.list.model.CharactersFilterModel
import ru.example.gnt.common.R
import ru.example.gnt.common.exceptions.ApplicationException
import ru.example.gnt.common.model.Resource
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.local.entity.CharacterEntity
import ru.example.gnt.data.mapper.CharacterEntityResponseMapper
import ru.example.gnt.data.remote.service.CharacterService

@ExperimentalPagingApi
class CharacterRemoteMediator @AssistedInject constructor(
    private val charactersDao: CharactersDao,
    private val service: CharacterService,
    @Assisted
    private val filterModel: CharactersFilterModel?,
    private val characterMapper: CharacterEntityResponseMapper
) : RemoteMediator<Int, CharacterEntity>() {

    private var pageIndex = 0
    private var limit = 0
    private var offset = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult = withContext(Dispatchers.IO) {
        pageIndex = getPageIndex(loadType)
            ?: return@withContext MediatorResult.Success(endOfPaginationReached = true)

        limit = state.config.pageSize
        offset = pageIndex * limit

        return@withContext try {
            val networkResult =
                service.getCharactersByPageFiltered(
                    page = pageIndex.toString(),
                    name = filterModel?.name,
                    species = filterModel?.species,
                    type = filterModel?.type,
                    status = filterModel?.status?.value,
                    gender = filterModel?.gender?.value
                ).awaitResponse()
            val characters = networkResult.body()?.results
            if (networkResult.isSuccessful && characters != null) {
                if (loadType == LoadType.REFRESH) {
                    with(filterModel) {
                        charactersDao.refresh(
                            name = this?.name,
                            species = this?.species,
                            type = this?.type,
                            status = this?.status?.value,
                            gender = this?.gender?.value,
                            characters = characters.map(characterMapper::mapTo)
                        )
                    }
                } else {
                    charactersDao.saveCharacters(
                        characters.map(
                            characterMapper::mapTo
                        )
                    )
                }
                MediatorResult.Success(
                    endOfPaginationReached = ((networkResult.body())?.results?.size
                        ?: 0) < limit
                )
            } else {
                MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (ex: ApplicationException.ConnectionException) {
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (ex: Exception) {
            MediatorResult.Error(
                ApplicationException.DataAccessException(
                    resource = Resource.String(
                        R.string.data_access_error
                    )
                )
            )
        }
    }

    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return null
            LoadType.APPEND -> ++pageIndex
        }
        return pageIndex
    }

    @AssistedFactory
    interface CharactersRemoteMediatorFactory {
        @OptIn(ExperimentalPagingApi::class)
        fun create(filterModel: CharactersFilterModel): CharacterRemoteMediator
    }

}
