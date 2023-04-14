package ru.example.gnt.characters.data

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.data.mapper.CharacterUiEntityMapper
import ru.example.gnt.common.model.ui.characters.CharactersUiModel
import javax.inject.Inject

@ExperimentalPagingApi
class CharactersRepositoryImpl @Inject constructor(
    private val charactersDao: CharacterDao,
    private val mediator: CharacterRemoteMediator
) : CharactersRepository {

    override fun getCharacters(): Flow<PagingData<CharactersUiModel.Single>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE
            ),
            remoteMediator = mediator,
            pagingSourceFactory = {
                charactersDao.getCharacters()
            }
        )
            .flow
            .map { pagingData ->
                pagingData.map {
                    CharacterUiEntityMapper.mapTo(it)
                }
            }
    }

    private companion object {
        private const val PAGE_SIZE = 6
    }

}
