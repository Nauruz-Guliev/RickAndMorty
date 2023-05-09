package ru.example.gnt.characters.data

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.characters.presentation.list.model.CharactersFilterModel
import ru.example.gnt.common.model.characters.CharacterListItem
import ru.example.gnt.data.local.dao.CharactersDao
import ru.example.gnt.data.mapper.CharacterEntityUiListItemMapper
import javax.inject.Inject

@ExperimentalPagingApi
class CharacterListRepositoryImpl @Inject constructor(
    private val charactersDao: CharactersDao,
    private val factory: CharacterRemoteMediator.CharactersRemoteMediatorFactory,
    private val mapper: CharacterEntityUiListItemMapper,
) : CharactersRepository {
    override suspend fun getCharacters(filterModel: CharactersFilterModel): Flow<PagingData<CharacterListItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
            ),
            remoteMediator = getMediator(filterModel),
            pagingSourceFactory = {
                with(filterModel) {
                    charactersDao.getCharactersFilteredPaged(
                        name = name,
                        species = species,
                        type = type,
                        status = status?.value,
                        gender = gender?.value
                    )
                }
            }
        )
            .flow
            .map { pagingData ->
                pagingData.map (mapper::mapTo)
            }
    }


    private fun getMediator(filterModel: CharactersFilterModel): CharacterRemoteMediator {
        return factory.create(filterModel)
    }

    private companion object {
        private const val PAGE_SIZE = 4
    }

}
