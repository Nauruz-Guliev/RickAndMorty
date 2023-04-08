package ru.example.gnt.characters.data.repository

import kotlinx.coroutines.flow.Flow
import ru.example.gnt.characters.domain.model.CharactersUiModel
import ru.example.gnt.characters.domain.repository.CharactersRepository


internal class CharactersRepositoryImpl : CharactersRepository {
    override fun getAllCharacters(): Flow<Result<CharactersUiModel>> {
        TODO("Not yet implemented")
    }

    override fun getCharacterById(id: Int): Flow<Result<CharactersUiModel.Single>> {
        TODO("Not yet implemented")
    }

    override fun getMultipleCharacters(ids: Array<Int>): Flow<Result<List<CharactersUiModel.Single>>> {
        TODO("Not yet implemented")
    }

}
