package ru.example.gnt.characters.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.await
import ru.example.gnt.characters.data.mapper.CharacterSingleUIMapper
import ru.example.gnt.characters.data.mapper.CharactersUiMapper
import ru.example.gnt.characters.domain.model.CharactersUiModel
import ru.example.gnt.characters.domain.repository.CharactersRepository
import ru.example.gnt.common.data.remote.RickAndMortyApi
import javax.inject.Inject

internal class CharactersRepositoryImpl @Inject constructor(
    private val api: RickAndMortyApi
) : CharactersRepository {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getAllCharacters(): Flow<Result<CharactersUiModel>> =
        withContext(dispatcher) {
            flow {
                emit(
                    Result.success(
                        CharactersUiMapper.mapTo(
                            api.getAllCharacters().await()
                        )
                    )
                )
            }
        }

    override suspend fun getCharacterById(id: Int): Flow<Result<CharactersUiModel.Single>> =
        withContext(dispatcher) {
            flow {
                emit(
                    Result.success(
                        CharacterSingleUIMapper.mapTo(api.getCharacterById(id).await())
                    )
                )
            }
        }

    override suspend fun getMultipleCharacters(ids: Array<Int>): Flow<Result<List<CharactersUiModel.Single>>> {
        TODO("Not yet implemented")
    }

}



