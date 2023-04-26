package ru.example.gnt.characters.data

import android.content.Context
import androidx.paging.*
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.example.gnt.characters.data.mapper.CharacterDtoUiMapper
import ru.example.gnt.characters.data.mapper.CharacterUiEntityMapper
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.characters.presentation.list.model.CharactersFilterModel
import ru.example.gnt.characters.presentation.list.model.CharactersUiModel
import ru.example.gnt.data.local.dao.CharacterDao
import ru.example.gnt.data.remote.service.CharacterService
import javax.inject.Inject

@ExperimentalPagingApi
class CharactersRepositoryImpl @Inject constructor(
    private val charactersDao: CharacterDao,
    private val factory: CharacterRemoteMediator.CharactersRemoteMediatorFactory,
    private val characterApi: CharacterService,
    private val mapper: CharacterUiEntityMapper,
    private val context: Context,
) : CharactersRepository {
    override fun getCharacterById(id: Int): Observable<CharactersUiModel.Single> {
        return characterApi.getCharacterById(id).map(CharacterDtoUiMapper::mapTo)
    }

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
            .map { pagingData -> pagingData.map(mapper::mapTo) }
    }

    private fun getMediator(filterModel: CharactersFilterModel): CharacterRemoteMediator {
        return factory.create(filterModel)
    }

    private companion object {
        private const val PAGE_SIZE = 4
    }

}
