package ru.example.gnt.characters.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.*
import ru.example.gnt.characters.presentation.list.model.CharactersFilterModel
import ru.example.gnt.common.exceptions.ConnectionException
import ru.example.gnt.common.exceptions.DataAccessException
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
            if (networkResult.isSuccessful) {
                if (loadType == LoadType.REFRESH) {
                    with(filterModel) {
                        charactersDao.refresh(
                            name = this?.name,
                            species = this?.species,
                            type = this?.type,
                            status = this?.status?.value,
                            gender = this?.gender?.value,
                            characters = networkResult.body()!!.results!!.map(characterMapper::mapTo)
                        )
                    }
                } else {
                    charactersDao.saveCharacters(
                        networkResult.body()!!.results!!.map(
                            characterMapper::mapTo
                        )
                    )
                }
                MediatorResult.Success(endOfPaginationReached = (networkResult.body())!!.results!!.size < limit)
            } else {
                MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (ex: ConnectionException) {
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (ex: Exception) {
            MediatorResult.Error(DataAccessException(resource = Resource.String(ru.example.gnt.common.R.string.data_access_error)))
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
