package ru.example.gnt.characters.data

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.characters.presentation.characters.CharactersFilterModel
import ru.example.gnt.common.data.local.dao.CharacterDao
import ru.example.gnt.common.data.mapper.CharacterUiEntityMapper
import ru.example.gnt.common.model.ui.characters.CharactersUiModel
import javax.inject.Inject

@ExperimentalPagingApi
class CharactersRepositoryImpl @Inject constructor(
    private val charactersDao: CharacterDao,
    private val factory: CharacterRemoteMediator.CharactersRemoteMediatorFactory,
) : CharactersRepository {

    override suspend fun getCharacters(filterModel: CharactersFilterModel): Flow<PagingData<CharactersUiModel.Single>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
            ),
            remoteMediator = getMediator(filterModel),
            pagingSourceFactory = {
                charactersDao.getCharactersFilteredPaged(
                    name = filterModel.name,
                    species = filterModel.species,
                    type = filterModel.type,
                    status = filterModel.status?.get,
                    gender = filterModel.gender?.n
                )
            }
        )
            .flow
            .map { pagingData ->
                pagingData.map(CharacterUiEntityMapper::mapTo)
            }
    }

    private fun getMediator(filterModel: CharactersFilterModel): CharacterRemoteMediator {
        return factory.create(filterModel)
    }

    private companion object {
        private const val PAGE_SIZE = 4
    }

}
