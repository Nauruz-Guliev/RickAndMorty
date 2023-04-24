package ru.example.gnt.characters.data

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.*
import ru.example.gnt.characters.R
import ru.example.gnt.characters.presentation.list.model.CharactersFilterModel
import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.data.local.entity.CharacterEntity
import ru.example.gnt.common.data.mapper.CharacterEntityDtoMapper
import ru.example.gnt.common.data.remote.service.CharacterService
import ru.example.gnt.common.exceptions.NetworkConnectionException
import ru.example.gnt.common.exceptions.NetworkException
import ru.example.gnt.common.isNetworkOn
import ru.example.gnt.common.model.Resource

@ExperimentalPagingApi
class CharacterRemoteMediator @AssistedInject constructor(
    private val characterDao: CharacterDao,
    private val characterService: CharacterService,
    @Assisted private val filterModel: CharactersFilterModel?,
    private val context: Context
) : RemoteMediator<Int, CharacterEntity>() {

    private var pageIndex = 0

    var limit = 0
        private set
    var offset = 0
        private set

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        pageIndex = getPageIndex(loadType)
            ?: return MediatorResult.Success(endOfPaginationReached = true)
        limit = state.config.pageSize
        offset = pageIndex * limit

        fetchLaunches(limit = limit, offset = offset)
            .onFailure {
                return MediatorResult.Error(it)
            }
            .onSuccess { characters ->
                if (loadType == LoadType.REFRESH) {
                    characterDao.refresh(
                        characters = characters ?: listOf(),
                        name = filterModel?.name,
                        species = filterModel?.species,
                        type = filterModel?.type,
                        gender = filterModel?.gender?.n,
                        status = filterModel?.status?.get
                    )
                } else {
                    characterDao.saveCharacters(characters ?: listOf())
                }
                return MediatorResult.Success(
                    endOfPaginationReached = (characters ?: listOf()).size < limit
                )
            }
        return MediatorResult.Error(NetworkException())
    }


    private suspend fun fetchLaunches(
        limit: Int,
        offset: Int
    ): Result<List<CharacterEntity>?> {
        return try {
            if (context.isNetworkOn()) {
                val response = characterService.getCharactersByPageFiltered(
                    page = pageIndex.toString(),
                    name = filterModel?.name,
                    species = filterModel?.species,
                    type = filterModel?.type,
                    status = filterModel?.status?.get,
                    gender = filterModel?.gender?.n
                ).awaitResponse()
                Result.success(response.body()?.results?.map(CharacterEntityDtoMapper::mapTo))
            } else {
                Result.success(
                    characterDao.getCharactersFiltered(
                        name = filterModel?.name,
                        species = filterModel?.species,
                        type = filterModel?.type,
                        status = filterModel?.status?.get,
                        gender = filterModel?.gender?.n,
                        limit = limit,
                        offset = offset
                    )
                )
            }
        } catch (ex: Exception) {
            Result.failure(NetworkConnectionException(resource = Resource.String(R.string.unknown_network_error)))
        }


        /*

        .enqueue(object : Callback<Characters> {
        override fun onResponse(call: Call<Characters>, response: Response<Characters>) {
            result = if (response.isSuccessful && response.code() == 200) {
                Result.success(response.body()?.results?.map(CharactersEntityDtoMapper::mapTo))
            } else {
                Result.failure(NetworkException(message = Resource.String(R.string.unknown_network_error)))
            }
        }
        override fun onFailure(call: Call<Characters>, t: Throwable) {
            result =
                Result.failure(NetworkException(message = Resource.String(R.string.unknown_network_error)))
        }
    })

         */
        /*
    } else {
        result = try {
            Result.success(
                characterDao.getCharactersFiltered(
                    name = filterModel?.name,
                    species = filterModel?.species,
                    type = filterModel?.type,
                    status = filterModel?.status?.get,
                    gender = filterModel?.gender?.n,
                    limit = limit,
                    offset = offset
                )
            )
        } catch (ex: Exception) {
            Result.failure(DatabaseException())
        }
    }
         */
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
