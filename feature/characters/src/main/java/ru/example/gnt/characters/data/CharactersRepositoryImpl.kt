package ru.example.gnt.characters.data

import androidx.paging.*
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.awaitResponse
import ru.example.gnt.characters.data.mapper.CharacterDtoUiMapper
import ru.example.gnt.characters.data.mapper.CharacterUiEntityMapper
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.characters.presentation.list.model.CharactersFilterModel
import ru.example.gnt.characters.presentation.list.model.CharactersUiModel
import ru.example.gnt.common.model.episodes.EpisodeListItem
import ru.example.gnt.common.utils.ApiListQueryGenerator
import ru.example.gnt.data.local.dao.CharacterDao
import ru.example.gnt.data.mapper.EpisodeResponseUiListItemMapper
import ru.example.gnt.data.remote.service.CharacterService
import ru.example.gnt.data.remote.service.EpisodeService
import javax.inject.Inject

@ExperimentalPagingApi
class CharactersRepositoryImpl @Inject constructor(
    private val charactersDao: CharacterDao,
    private val factory: CharacterRemoteMediator.CharactersRemoteMediatorFactory,
    private val characterApi: CharacterService,
    private val mapper: CharacterUiEntityMapper,
    private val characterDtoUiMapper: CharacterDtoUiMapper,
    private val episodeService: EpisodeService,
    private val apiListQueryGenerator: ApiListQueryGenerator,
    private val episodeUiListItemMapper: EpisodeResponseUiListItemMapper
) : CharactersRepository {
    override fun getCharacterById(id: Int): Observable<CharactersUiModel.Single> {
        return characterApi.getCharacterById(id).map(characterDtoUiMapper::mapTo)
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

    override suspend fun getEpisodeList(ids: List<String>): List<EpisodeListItem> {
        return episodeService.getEpisodesInRange(apiListQueryGenerator.generate(ids))
            .awaitResponse()
            .body()!!.map {
                episodeUiListItemMapper.mapTo(it)
            }
    }

    private fun getMediator(filterModel: CharactersFilterModel): CharacterRemoteMediator {
        return factory.create(filterModel)
    }

    private companion object {
        private const val PAGE_SIZE = 4
    }

}
