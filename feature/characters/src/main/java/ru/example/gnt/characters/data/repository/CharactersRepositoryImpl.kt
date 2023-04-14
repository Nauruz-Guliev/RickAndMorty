package ru.example.gnt.characters.data.repository

/*
internal class CharactersRepositoryImpl @Inject constructor(
    private val api: CharacterService
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

    override suspend fun getFilteredCharacters(status: String?, gender: String?): Flow<Result<CharactersUiModel>> =
        withContext(dispatcher) {
            flow {
                emit(
                    Result.success(
                        CharactersUiMapper.mapTo(
                            api.getFilteredCharacters(status = status, gender = gender).await()
                        )
                    )
                )
            }
        }

}

 */



