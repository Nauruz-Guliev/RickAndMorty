package ru.example.gnt.characters.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import retrofit2.await
import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.data.local.entity.CharacterEntity
import ru.example.gnt.common.data.mapper.CharactersEntityDtoMapper
import ru.example.gnt.common.data.remote.service.CharacterService
import ru.example.gnt.common.utils.ApiQueryGenerator
import javax.inject.Inject

@ExperimentalPagingApi
class CharacterRemoteMediator @Inject constructor(
    private val characterDao: CharacterDao,
    private val characterService: CharacterService
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
            Log.d("CHARACTERS", "ha:" + characters.toString())
            if (loadType == LoadType.REFRESH) {
                characterDao.refresh(characters)
            } else {
                characterDao.saveCharacters(characters)
            }
            MediatorResult.Success(
                endOfPaginationReached = characters.size < limit
            )
        } catch (e: Exception) {
            Log.d("CHARACTERS", e.toString())
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
        return characterService.getCharactersInRange(query).await()
            .map { CharactersEntityDtoMapper.mapTo(it) } ?: listOf()
    }


    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return null
            LoadType.APPEND -> ++pageIndex
        }
        return pageIndex
    }

}
