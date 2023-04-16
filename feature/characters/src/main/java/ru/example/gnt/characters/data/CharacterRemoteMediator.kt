package ru.example.gnt.characters.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import retrofit2.await
import ru.example.gnt.characters.presentation.characters.CharactersFilterModel
import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.data.local.entity.CharacterEntity
import ru.example.gnt.common.data.mapper.CharactersEntityDtoMapper
import ru.example.gnt.common.data.remote.service.CharacterService
import ru.example.gnt.common.utils.ApiQueryGenerator

@ExperimentalPagingApi
class CharacterRemoteMediator @AssistedInject constructor(
    private val characterDao: CharacterDao,
    private val characterService: CharacterService,
    @Assisted private val filterModel: CharactersFilterModel?
) : RemoteMediator<Int, CharacterEntity>() {

    private var pageIndex = 0

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        pageIndex =
            getPageIndex(loadType)
                ?: return MediatorResult.Success(endOfPaginationReached = true)

        val limit = state.config.pageSize
        val offset = pageIndex * limit

        return try {
            val characters = fetchLaunches(limit, offset)
            if (loadType == LoadType.REFRESH) {
                characterDao.refresh(characters)
            } else {
                characterDao.saveCharacters(characters)
            }
            MediatorResult.Success(
                endOfPaginationReached = characters.size < limit
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }


    private suspend fun fetchLaunches(
        limit: Int,
        offset: Int
    ): List<CharacterEntity> {
        val query = ApiQueryGenerator.generateCharacterIdsQuery(
            limit = limit,
            offset = offset
        )
        Log.d("FILTER_MODEL", filterModel.toString())
        return try {
            filterResults(characterService.getCharactersInRange(query).await()
                .map { CharactersEntityDtoMapper.mapTo(it) })
        } catch (ex: Exception) {
            filterResults(characterDao.getCharactersInRage(limit, offset))
        }
    }

    private fun filterResults(list: List<CharacterEntity>): List<CharacterEntity> {
        Log.d("FILTER", filterModel?.gender.toString())
        return list.filter {
            // фильтруем по статусу
                    filterModel?.gender?.n.equals(it.gender, ignoreCase = true) ?: true
        }.filter {
            filterModel?.status?.get?.equals(it.status, ignoreCase = true) ?: true
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
        fun create(filterModel: CharactersFilterModel?): CharacterRemoteMediator
    }

}
